#include "SerialProtocol.h"

#include <string.h>


SerialProtocol::SerialProtocol() {

}

void SerialProtocol::setStream(Stream &stream) {
    mStream = &stream;
}

void SerialProtocol::update() {
    if (millis() - mLastTime >= 20) {
        mLastTime = millis();

        if (mStream->available()) {
            int length = mStream->available();
            length = min(length, mBuffer.available());

            char temp[64] = {0};
            mStream->readBytes(temp, length);
            // Serial.print(temp);

            for (int i = 0; i < length; i++) {
                mBuffer.push(temp[i]);
            }

            processPacket();
        }
    }
}

void SerialProtocol::processPacket() {
    if (!mReceived) {
        while (!mBuffer.isEmpty()) {
            switch (mBuffer.first()) {
            case 1:
                // Serial.println(F("SOH"));
                mPacketStart = false;
                mContent[0] = 0;
                mIndex = 0;
                sendACK();
                break;
            case 2:
                // Serial.println(F("STX"));
                mPacketStart = true;
                break;
            case 3:
                // Serial.println(F("ETX"));
                mPacketStart = false;
                sendACK();
                break;
            case 4:
                // Serial.println(F("EOT"));
                // Serial.println(mContent);
                mContent[mIndex++] = 0;
                mReceived = true;
                sendACK();
                break;
            default:
                if (mPacketStart) {
                    // Serial.print(mBuffer.first());
                    mContent[mIndex++] = mBuffer.first();
                }
                break;
            }
            mBuffer.shift();

            // Serial.print(mBuffer.shift());
        }
    }
}

void SerialProtocol::sendACK() {
    mStream->write(6);
}

bool SerialProtocol::available() {
    return mReceived;
}

char *SerialProtocol::receive() {
    mReceived = false;
    return mContent;
}