<template lang="pug">
div
    v-app-bar(app)
        v-btn(icon, @click="$router.back()")
            v-icon mdi-arrow-left

        v-toolbar-title 設定

    v-card.content-wrap
        v-list(two-line, subheader)
            v-subheader 連線
            v-list-item(nuxt, to="/setting/device")
                v-list-item-avatar
                    v-icon mdi-cellphone-link
                v-list-item-content
                    v-list-item-title 連線裝置
                    v-list-item-subtitle {{ deviceLabel }}
                v-list-item-action
                    v-icon mdi-chevron-right

            //- v-list-item
                v-list-item-avatar
                    v-icon 
                v-list-item-content
                    v-list-item-title 自動配對
                v-list-item-action
                    v-switch

            //- v-list-item(disabled)
                v-list-item-avatar
                    v-icon mdi-form-textbox-password
                v-list-item-content
                    v-list-item-title PIN
                    v-list-item-subtitle ****
                v-list-item-action
                    v-icon mdi-pencil

            v-divider
            v-subheader 自動
            v-list-item
                v-list-item-avatar
                    v-avatar(size="32")
                        v-img(src="~/assets/connect.svg")
                v-list-item-content
                    v-list-item-title 自動連線
                    v-list-item-subtitle 
                v-list-item-action
                    v-switch

            v-list-item
                v-list-item-avatar
                    v-icon mdi-bluetooth
                v-list-item-content
                    v-list-item-title 自動開啟藍芽
                    v-list-item-subtitle 
                v-list-item-action
                    v-switch

            v-list-item
                v-list-item-avatar
                    v-icon mdi-bluetooth
                v-list-item-content
                    v-list-item-title 自動關閉藍芽
                    v-list-item-subtitle 
                v-list-item-action
                    v-switch
</template>

<script lang="ts">
import { Vue, Component } from 'nuxt-property-decorator'

@Component({ transition: 'slide-y-transition' })
export default class extends Vue {
    get deviceLabel() {
        return this.$accessor.setting.deviceLabel
    }

    mounted() {
        
    }
}
</script>