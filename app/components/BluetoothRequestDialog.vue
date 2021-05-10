<template lang="pug">
v-dialog(persistent, origin="bottom center", v-model="show")
    v-card
        v-card-title
            v-icon(color="warning") mdi-alert-outline
            span.ml-1 開啟藍芽
        v-card-text.text-subtitle 本軟體需要開起藍芽才可使用
        v-card-actions
            v-spacer
            v-btn(outlined, color="error", @click="exit") 離開
            v-btn(
                outlined,
                color="success",
                :loading="enabling",
                @click="enable",
                v-if="isAndroid"
            ) 開啟
</template>

<script lang="ts">
import { Vue, Component } from 'nuxt-property-decorator'
import { BluetoothState } from '~/internal/enum'

@Component
export default class extends Vue {
    enabling = false

    get show() {
        return this.$accessor.btState !== BluetoothState.On
    }

    get isAndroid() {
        return this.$accessor.platform === 'android'
    }

    exit() {
        $native.bluetooth.disable()
        $native.application.exit()
    }

    async enable() {
        this.enabling = true
        await $native.bluetooth.enable()
        this.enabling = false
    }
}
</script>