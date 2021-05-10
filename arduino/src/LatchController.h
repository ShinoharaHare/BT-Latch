#include <Bounce2.h>

class LatchController {
    enum Signal {
        Stop,
        Lock,
        Unlock
    };

    enum State {
        None,
        Locked,
        Unlocked,
        Middle,
        Locking,
        Unlocking
    };
public:
    LatchController();

    void update();

    void lock();
    void unlock();
    
    State state();
    bool isLocked();
    bool isUnlocked();

    static LatchController &instance();

private:
    Bounce mLockedSwitch;
    Bounce mUnlockedSwitch;
    bool mLockFlag = false;
    bool mUnlockFlag = false;
    State mState = None;

    void writeMotor(Signal signal);
};
