export enum ConnectionState {
    None,
    Connecting,
    Connected
}

export enum DeviceType {
    MISC = 0x0000,
    COMPUTER = 0x0100,
    PHONE = 0x0200,
    NETWORKING = 0x0300,
    AUDIO_VIDEO = 0x0400,
    PERIPHERAL = 0x0500,
    IMAGING = 0x0600,
    WEARABLE = 0x0700,
    TOY = 0x0800,
    HEALTH = 0x0900,
    UNCATEGORIZED = 0x1F00,
}

export enum BluetoothState {
    On,
    Off,
    TurningOn,
    TurningOff
}

export enum LatchState {
    None,
    Locked,
    Unlocked,
    Middle,
    Locking,
    Unlocking
}