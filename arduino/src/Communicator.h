#pragma once
#include "config.h"
#include "SerialProtocol.h"

#include <Arduino.h>
#include <ArduinoJson.h>
#include <SoftwareSerial.h>


class Communicator
{
    using JSONBuffer = StaticJsonDocument<256>;

public:
    Communicator();

    void update();

    static Communicator &instance();

private:
    SoftwareSerial mBTSerial;
    SerialProtocol mSerialProtocol;
    JSONBuffer mRequestJson;
    JSONBuffer mResponseJson;

    void processRequest();
};
