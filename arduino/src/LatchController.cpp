#include "LatchController.h"
#include "pin.h"

LatchController::LatchController() {
    mLockedSwitch.attach(PIN_LOCKED, INPUT);
    mUnlockedSwitch.attach(PIN_UNLOCKED, INPUT);
}

void LatchController::update() {
    mLockedSwitch.update();
    mUnlockedSwitch.update();

    // Serial.print("locked: ");
    // Serial.println(mLockedSwitch.read());

    // Serial.print("unlocked: ");
    // Serial.println(mUnlockedSwitch.read());

    switch (mState) {
    case None:
        if (mLockedSwitch.read()) {
            mState = Locked;
        } else if (mUnlockedSwitch.read()) {
            mState = Unlocked;
        } else {
            mState = Middle;
        }
        break;
    case Middle:
        if (mUnlockFlag) {
            writeMotor(Unlock);
            mUnlockFlag = false;
            mState = Unlocking;
        } else if (mLockFlag) {
            writeMotor(Lock);
            mLockFlag = false;
            mState = Locking;
        }
        break;
    case Locked:
        if (mUnlockFlag) {
            writeMotor(Unlock);
            mUnlockFlag = false;
            mState = Unlocking;
        }
        break;
    case Unlocking:
        if (mUnlockedSwitch.read()) {
            writeMotor(Stop);
            mState = Unlocked;
        }
        break;
    case Unlocked:
        if (mLockFlag) {
            writeMotor(Lock);
            mLockFlag = false;
            mState = Locking;
        }
        break;
    case Locking:
        if (mLockedSwitch.read()) {
            writeMotor(Stop);
            mState = Locked;
        }
        break;
    }

    if (mLockedSwitch.read()) {
        mState = Locked;
    } else if (mUnlockedSwitch.read()) {
        mState = Unlocked;
    }
}

void LatchController::lock() {
    mLockFlag = true;
}

void LatchController::unlock() {
    mUnlockFlag = true;
}

bool LatchController::isLocked() {
    return mState == Locked;
}

bool LatchController::isUnlocked() {
    return mState == Unlocked;
}

LatchController::State LatchController::state() {
    return mState;
}

void LatchController::writeMotor(Signal signal) {
    switch (signal) {
    case Stop:
        digitalWrite(PIN_MOTOR_IN1, LOW);
        digitalWrite(PIN_MOTOR_IN2, LOW);
        break;
    case Lock:
        digitalWrite(PIN_MOTOR_IN1, LOW);
        digitalWrite(PIN_MOTOR_IN2, HIGH);
        break;
    case Unlock:
        digitalWrite(PIN_MOTOR_IN1, HIGH);
        digitalWrite(PIN_MOTOR_IN2, LOW);
        break;
    }
}

LatchController &LatchController::instance() {
    static LatchController instance;
    return instance;
}
