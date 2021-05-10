import { getAccessorType, getterTree, mutationTree, actionTree } from 'typed-vuex'
import { delay, getNuxt, onNativeReady } from '~/internal/util'
import { BluetoothState, ConnectionState, LatchState } from '~/internal/enum'

import setting from './setting'

const state = () => ({
    platform: <'android' | 'ios' | null>null,
    btState: BluetoothState.On,
    connection: ConnectionState.None,
    latchState: LatchState.None
})

const getters = getterTree(state, {
    disconnected(state) {
        return state.connection == ConnectionState.None
    },
    connected(state) {
        return state.connection == ConnectionState.Connected
    },
    connecting(state) {
        return state.connection == ConnectionState.Connecting
    },
    locked(state) {
        return state.latchState == LatchState.Locked
    },
    unlocked(state) {
        return state.latchState == LatchState.Unlocked
    }
})

const mutations = mutationTree(state, {
    setPlatform(state, value: 'android' | 'ios') {
        state.platform = value
    },
    setBtState(state, value: BluetoothState) {
        state.btState = value
    },
    setConnection(state, value: ConnectionState) {
        state.connection = value
    },
    setLatchState(state, value: LatchState) {
        state.latchState = value
    }
})

const actions = actionTree({ state, getters, mutations }, {
    disconnect() {
        
    }
})

onNativeReady(async (native) => {
    const $nuxt = await getNuxt()
    const store = $nuxt.$accessor

    store.setPlatform(native.platform)

    native.on('bluetoothStateChanged', event => {
        store.setBtState(event.detail)
    })

    native.on('connectionStateChange', async (event) => {
        store.setConnection(event.detail)

        while (store.connected) {
            let v = await native.latch.state()
            store.setLatchState(v)
            // console.log(v)
            await delay(1000)
        }
    })

    native.bluetooth
        .isEnabled()
        .then(v => {
            store.setBtState(v ?
                BluetoothState.On :
                BluetoothState.Off
            )
        })

    native.latch
        .connection()
        .then(v => store.setConnection(v))
})

const accessor = getAccessorType({
    state,
    getters,
    mutations,
    actions,
    modules: {
        setting
    }
})

export type IAccessor = typeof accessor

export default {
    state,
    getters,
    mutations,
    actions
}