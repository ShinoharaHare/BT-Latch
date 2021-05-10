package io.shinoharahare.btlatch

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.KeyEvent
import android.webkit.WebView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.shinoharahare.btlatch.JSModule.BluetoothBridge
import io.shinoharahare.btlatch.JSModule.LatchBridge
import io.shinoharahare.btlatch.JSModule.ApplicationBridge
import io.shinoharahare.jsbridge.JSBridge
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {
    private val mWebView: WebView by lazy { findViewById<WebView>(R.id.webView) }
    private val mJSBridge: JSBridge = JSBridge()
    private val mBTReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val state =
                when (intent?.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1)) {
                    BluetoothAdapter.STATE_ON -> BluetoothState.On.ordinal
                    BluetoothAdapter.STATE_OFF -> BluetoothState.Off.ordinal
                    BluetoothAdapter.STATE_TURNING_ON -> BluetoothState.TurningOn.ordinal
                    BluetoothAdapter.STATE_TURNING_OFF -> BluetoothState.TurningOff.ordinal
                    else -> BluetoothState.Off.ordinal
                }
            mJSBridge.emit("bluetoothStateChanged", state)
        }
    }
    private var mBackPressed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        WebView.setWebContentsDebuggingEnabled(true)
        mWebView.settings.domStorageEnabled = true

        mJSBridge.setup(mWebView)
        mJSBridge.addModule("bluetooth", BluetoothBridge)
        mJSBridge.addModule("application", ApplicationBridge)
        mJSBridge.addModule("latch", LatchBridge)
        mWebView.loadUrl("file:///android_asset/ui/index.html")
//        mWebView.loadUrl("http://192.168.137.1:3000")
        registerReceiver(mBTReceiver, IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED))

        Communicator.on("stateChange") {
            val state = it as Communicator.State
            GlobalScope.launch(Dispatchers.Main) {
                mJSBridge.emit("connectionStateChange", state.ordinal)
            }
        }

//        GlobalScope.launch {
//            val success = Communicator.connect("00:19:10:08:FF:F3")
//            if (success) {
//                println("Connected")
//                Communicator.request("abababababababababababababababababababababababababababababababababababababababababababababababababababababababababababababababab")
//            } else {
//                println("Failed to Connect")
//            }
//        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mBTReceiver)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_BACK -> {
//                if (mJSBridge.ready) {
//                    mJSBridge.emit("back", mWebView.canGoBack())
//                    return true
//                }
                if (mWebView.canGoBack()) {
                    mWebView.goBack()
                } else {
                    if (mBackPressed) {
                        exitProcess(0)
                    } else {
                        Toast.makeText(this, "再按一次退出程式", Toast.LENGTH_SHORT).show()
                        GlobalScope.launch {
                            mBackPressed = true
                            delay(2000)
                            mBackPressed = false
                        }
                    }
                }
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}
