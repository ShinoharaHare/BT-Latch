#include "Communicator.h"
#include "LatchController.h"
#include "pin.h"


#if defined(DEBUG)
#define mBTSerial Serial
#endif

Communicator::Communicator() : mBTSerial(PIN_BT_RX, PIN_BT_TX) {
    mBTSerial.begin(9600);
    mSerialProtocol.setStream(mBTSerial);
}

void Communicator::update() {
    mSerialProtocol.update();
    if (mSerialProtocol.available()) {
        char *content = mSerialProtocol.receive();
        deserializeJson(mRequestJson, content);
        processRequest();
    }
}

void Communicator::processRequest() {
    mResponseJson["state"] = "ok";
    mResponseJson["_id"] = mRequestJson["_id"];

    auto command = mRequestJson["command"];

    if (command == "lock") {
        LatchController::instance().lock();
    } else if (command == "unlock") {
        LatchController::instance().unlock();
    } else if (command == "isLocked") {
        bool v = LatchController::instance().isLocked();
        mResponseJson["data"] = v;
    } else if (command == "isUnlocked") {
        bool v = LatchController::instance().isUnlocked();
        mResponseJson["data"] = v;
    } else if (command == "state") {
        int v = LatchController::instance().state();
        mResponseJson["data"] = v;
    } else {
        mResponseJson["state"] = "unknown";
    }

    serializeJson(mResponseJson, mBTSerial);

    // Serial.print("Request: ");
    // Serial.print(mRequestJson["command"].as<const char *>());
    // // serializeJson(mRequestJson, Serial);
    // // Serial.println();
    // Serial.print("\tResponse: ");
    // Serial.println(mResponseJson["data"].as<int>());
    // // serializeJson(mResponseJson, Serial);
    // // Serial.println();

    mRequestJson.clear();
    mResponseJson.clear();
}

Communicator &Communicator::instance() {
    static Communicator instance;
    return instance;
}
