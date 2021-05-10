import { IAccessor } from '~/store'

declare module 'vue/types/vue' {
    interface Vue {
        $accessor: IAccessor
    }
}

declare module '@nuxt/types' {
    interface NuxtAppOptions {
        $accessor: IAccessor
    }
}