import { INative } from '~/types/native'

export function onNativeReady(callback: (native: INative) => void) {
    if (isNativeReady()) {
        callback(window.$native)
    } else {
        addEventListener('nativeReady', () => {
            callback(window.$native)
        })
    }
}

export async function getNuxt() {
    for (; ;) {
        if (window.$nuxt) {
            return window.$nuxt
        }
        await delay(300)
    }
}

export function isNativeReady() {
    return window.hasOwnProperty('$native')
}

export function delay(ms: number) {
    return new Promise(resolve => setTimeout(resolve, ms))
}
