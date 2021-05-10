<template lang="pug">
div
    v-app-bar(app)
        v-toolbar-title BT Latch
        v-spacer
        v-btn(icon, @click="reload", v-if="isDevelopment")
            v-icon mdi-refresh
        v-btn(icon, nuxt, to="/setting")
            v-icon mdi-cog-outline

    .content-wrap.text-center
        v-container(fluid, fill-height, style="height: 95%")
            v-row
                v-col.pb-0
                    transition(
                        mode="out-in",
                        enter-active-class="animate__animated animate__bounceIn",
                        leave-active-class="animate__animated animate__bounceOut",
                        :duration="{ leave: 750, enter: 500 }"
                    )
                        Padlock(
                            key="1",
                            v-if="connected"
                        )
                        ConnectButton(
                            key="2",
                            v-else
                        )

            v-row.mt-0
                v-col
                    StateTable
</template>

<script lang="ts">
import { Vue, Component } from 'nuxt-property-decorator'

import ConnectButton from '~/components/ConnectButton.vue'
import Padlock from '~/components/Padlock.vue'
import StateTable from '~/components/StateTable.vue'


@Component({ components: { ConnectButton, Padlock, StateTable } })
export default class extends Vue {
    get isDevelopment() {
        return process.env.NODE_ENV === 'development'
    }

    get connected() {
        return this.$accessor.connected
    }

    reload() {
        location.reload()
    }
}
</script>

<style scoped>
.v-list-item {
    height: 40px;
}

.v-list-item__content {
    width: 40px;
    text-overflow: ellipsis;
    white-space: nowrap;
}
</style>