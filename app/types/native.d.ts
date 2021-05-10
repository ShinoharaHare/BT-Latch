import { ConnectionState, LatchState } from '~/internal/enum'

interface INative {
    platform: 'android' | 'ios'
    // _call(mName: String, fName: string, args?: any): Promise<any>
    application: IApplication
    bluetooth: IBluetooth
    latch: ILatch
    on(event: string, callback: (event: CustomEvent) => void): void
}

interface IBluetooth {
    isAvailable(): Promise<boolean>
    isEnabled(): Promise<boolean>

    getBondedDevices(): Promise<any[]>
    enable(): Promise<void>
    disable(): Promise<void>
    scan(duration: number): Promise<any>
}

interface IApplication {
    exit(): Promise<void>
}

interface ILatch {
    connect(address: string): Promise<boolean>
    disconnect(): Promise<boolean>
    connection(): Promise<ConnectionState>
    isLocked(): Promise<boolean>
    isUnlocked(): Promise<boolean>
    state(): Promise<LatchState>
    lock(): Promise<void>
    unlock(): Promise<void>
}

declare global {
    const $native: INative

    interface Window {
        $native: INative
    }
}

declare module 'vue/types/vue' {
    interface Vue {
        $native: INative | null
    }
}
