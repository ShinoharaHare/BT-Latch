<template lang="pug">
div
    v-expand-transition
        v-card(width="100%", v-if="connected")
            v-btn(block, outlined, color="red", @click="disconnect")
                v-avatar(size="24")
                    v-img(src="~/assets/disconnect.svg")
                span 中斷連線
    v-simple-table
        tbody
            tr
                td 連線狀態
                td 
                    v-list-item
                        v-list-item-avatar
                            v-progress-circular(
                                indeterminate,
                                size="24",
                                v-if="connectionState.spinner"
                            )
                            v-icon(:color="connectionState.color", v-else) {{ connectionState.icon }}
                        v-list-item-content {{ connectionState.text }}
            tr
                td 目標裝置
                td 
                    v-list-item(nuxt, to="/setting/device")
                        v-list-item-avatar
                            v-icon(color="blue") mdi-pencil-outline
                        v-list-item-content {{ deviceName }}
            tr
                td 門鎖狀態
                td
                    v-list-item
                        v-list-item-avatar
                            v-progress-circular(
                                indeterminate,
                                size="24",
                                v-if="latchState.spinner"
                            )
                            v-icon(:color="latchState.color", v-else) {{ latchState.icon }}
                        v-list-item-content {{ latchState.text }}
</template>

<script lang="ts">
import { Vue, Component } from 'nuxt-property-decorator'
import { ConnectionState, LatchState } from '~/internal/enum'

@Component
export default class extends Vue {
    get deviceName() {
        return this.$accessor.setting.deviceName
    }

    get connection() {
        return this.$accessor.connection
    }

    get connected() {
        return this.$accessor.connected
    }

    get connecting() {
        return this.$accessor.connecting
    }

    get connectionState() {
        switch (this.connection) {
            case ConnectionState.None:
                return {
                    text: '未連線',
                    color: 'red',
                    icon: 'mdi-close-circle-outline'
                }
            case ConnectionState.Connecting:
                return {
                    text: '連線中...',
                    spinner: true
                }
            case ConnectionState.Connected:
                return {
                    text: '已連線',
                    color: 'green',
                    icon: 'mdi-check-circle-outline'
                }
        }

    }

    get latchState() {
        if (this.connected) {
            switch (this.$accessor.latchState) {
                case LatchState.None:
                    break
                case LatchState.Locked:
                    return {
                        text: '已上鎖',
                        color: 'green',
                        icon: 'mdi-lock-check-outline',
                    }
                case LatchState.Unlocked:
                    return {
                        text: '已解鎖',
                        color: 'amber',
                        icon: 'mdi-lock-open-alert-outline',
                    }
                case LatchState.Middle:
                    return {
                        text: '未歸位',
                        color: 'red',
                        icon: 'mdi-alert-circle-outline',
                    }
                case LatchState.Locking:
                    return {
                        text: '上鎖中',
                        spinner: true
                    }
                case LatchState.Unlocking:
                    return {
                        text: '解鎖中',
                        spinner: true
                    }
            }
        }
        return {
            text: '未知',
            color: 'grey',
            icon: 'mdi-lock-question'
        }
    }

    async disconnect() {
        await $native.latch.disconnect()
    }
}
</script>

<style scoped>
.v-data-table tr:hover {
    background: unset !important;
}
</style>