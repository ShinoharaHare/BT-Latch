#include <Arduino.h>
#include <CircularBuffer.h>

class SerialProtocol
{

public:
    SerialProtocol();

    void setStream(Stream &stream);
    void update();

    bool available();
    char *receive();
    void send();

private:
    Stream *mStream;
    CircularBuffer<char, 64> mBuffer;
    char mContent[512];
    uint16_t mIndex = 0;

    bool mPacketStart = false;
    bool mReceived = false;
    unsigned long mLastTime = 0;

    void processPacket();
    void sendACK();
};
