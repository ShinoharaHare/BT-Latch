<template lang="pug">
v-app(dark)
    nuxt

    BluetoothRequestDialog
</template>

<script lang="ts">
import { Vue, Component } from 'nuxt-property-decorator'

import BluetoothRequestDialog from '~/components/BluetoothRequestDialog.vue'

@Component({ components: { BluetoothRequestDialog } })
export default class extends Vue {
    
}
</script>

<style lang="scss">
.content-wrap {
    height: calc(100vh - 56px);
    margin-top: 56px;
    overflow-x: hidden;
}
</style>