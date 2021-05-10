#include "Communicator.h"
#include "LatchController.h"


void setup() {
    Serial.begin(9600);
}

void loop() {
    LatchController::instance().update();
    Communicator::instance().update();
}
