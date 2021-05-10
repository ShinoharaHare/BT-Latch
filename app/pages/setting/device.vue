<template lang="pug">
div
    v-app-bar(app)
        v-btn(icon, @click="$router.back()")
            v-icon mdi-arrow-left

        v-toolbar-title 選擇裝置

    v-card.content-wrap
        v-list.pt-2(two-line, rounded, subheader)
            v-list-item-group(color="success", v-model="highlight")
                transition-group(
                    enter-active-class="animate__animated animate__lightSpeedInRight animate__faster"
                )
                    v-list-item(
                        v-for="(device, i) in items",
                        :key="device.address",
                        @click="select(i)"
                    )
                        v-list-item-avatar
                            v-icon {{ getDeviceIcon(device.class) }}
                        v-list-item-content
                            v-list-item-title {{ device.name }}
                            v-list-item-subtitle {{ device.address }}
                        v-list-item-action

        v-footer(fixed, height="72")
            v-card(outlined, style="margin-right: 56px")
                v-list-item(style="width: calc(100vw - 88px)")
                    v-list-item-content
                        v-list-item-title {{ deviceName }}
                        v-list-item-subtitle {{ deviceAddress }}
                //- v-card(outlined)
                    v-card-title.text-subtitle-2 {{ deviceName }}
                    v-card-text {{ deviceAddress }}

            v-btn(fab, small, absolute, right, color="primary", @click="reset")
                v-icon mdi-refresh

    v-dialog(eager, max-width="290", v-model="dialog")
        v-card
            v-card-title 儲存設定
            v-card-text 確定要將它設為目標裝置嗎?
            v-card-actions
                v-spacer
                v-btn(outlined, color="error", @click="dialog = false") 取消
                v-btn(outlined, color="success", @click="save") 儲存
</template>

<script lang="ts">
import { Vue, Component, Watch } from 'nuxt-property-decorator'

import { DeviceType } from '~/internal/enum';

@Component({ transition: 'slide-x-transition' })
export default class extends Vue {
    items: any[] = []
    fetching = false
    dialog = false
    selected = -1
    highlight = -1

    get deviceName() {
        return this.$accessor.setting.deviceName
    }

    get deviceAddress() {
        return this.$accessor.setting.deviceAddress
    }

    get current() {
        return this.items.findIndex(device => device.address === this.deviceAddress)
    }

    select(i: number) {
        this.selected = i
        this.dialog = true
    }

    save() {
        this.dialog = false
        this.$accessor.setting.setDevice(this.items[this.selected])
        this.highlight = this.selected
    }

    getDeviceIcon(type: any) {
        const map: any = {
            [DeviceType.COMPUTER]: 'mdi-monitor',
            [DeviceType.PHONE]: 'mdi-cellphone',
            [DeviceType.NETWORKING]: 'mdi-access-point-network',
            [DeviceType.AUDIO_VIDEO]: 'mdi-headphones',
            [DeviceType.PERIPHERAL]: 'mdi-printer-wireless',
            [DeviceType.IMAGING]: 'mdi-camera-wireless',
            [DeviceType.WEARABLE]: 'mdi-watch',
            [DeviceType.TOY]: 'mdi-controller-classic',
            [DeviceType.HEALTH]: 'mdi-heart-pulse',
            [DeviceType.UNCATEGORIZED]: 'mdi-bluetooth'
        }
        return map[type] || 'mdi-bluetooth'
    }

    reset() {
        if (this.fetching) {
            return
        }
        this.items = []
        this.fetchBondedDevices()
    }

    async fetchBondedDevices() {
        this.fetching = true
        let arr = await $native.bluetooth.getBondedDevices()
        for (let i of arr) {
            this.items.push(i)
            await new Promise(resolve => setTimeout(resolve, 150))
        }
        this.highlight = this.current
        this.fetching = false
    }

    @Watch('highlight')
    onCurrentChange(val: number) {
        let current = this.current
        if (val != current) {
            this.$nextTick(() => this.highlight = current)
        }
    }

    mounted() {
        this.reset()
    }
}
</script>

<style scoped>
.v-list {
    height: calc(100% - 72px);
    overflow: hidden scroll;
}

.v-list-item {
    /* width: calc(100vw - 88px); */
    text-overflow: ellipsis;
}
</style>

<style lang="scss">
::-webkit-scrollbar {
    width: 6px;
}

::-webkit-scrollbar-thumb {
    background-color: darkgrey;
}
</style>
