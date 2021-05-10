import { mutationTree, getterTree } from 'typed-vuex'


interface IDevice {
    name: string
    address: string
}

const _state = {
    device: <IDevice | null>null,
    autoBtOn: true,
    autoBtOff: true
}

type State = typeof _state
type Key = keyof State

function save(key: Key, value: any) {
    localStorage[key] = JSON.stringify(value)
}

const state = (): State => {
    const state: any = {}
    for (let k of Object.keys(_state)) {
        let key: Key = k as any
        let saved = localStorage[key]
        let parsed: any = null
        try { parsed = JSON.parse(saved) } catch (error) { }
        state[key] = _state[key] ?? parsed
    }
    return state
}

const getters = getterTree(state, {
    deviceName(state) {
        return state.device?.name ?? '尚未選擇'
    },
    deviceAddress(state) {
        return state.device?.address ?? ''
    },
    deviceLabel(state) {
        let device = state.device
        return device ? `${device?.name}(${device?.address})` : '尚未選擇'
    }
})

const mutations = mutationTree(state, {
    setDevice(state, value: IDevice) {
        state.device = value
        save('device', value)
    }
})

export default {
    state,
    mutations,
    getters
}
