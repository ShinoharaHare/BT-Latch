import { Plugin } from '@nuxt/types'
import { onNativeReady } from '~/internal/util'

const plugin: Plugin = (context, inject) => {
    inject('native', null)
    onNativeReady(native => inject('native', native))
}

export default plugin