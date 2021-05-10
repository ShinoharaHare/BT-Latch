package io.shinoharahare.btlatch.JSModule

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import io.shinoharahare.btlatch.Application
import io.shinoharahare.jsbridge.JSBridgeInterface
import io.shinoharahare.jsbridge.JSBridgeModule
import io.shinoharahare.jsbridge.JBCallback
import org.json.JSONArray
import org.json.JSONObject

object BluetoothBridge : JSBridgeModule() {
    private val adapter = BluetoothAdapter.getDefaultAdapter()

    @JSBridgeInterface
    fun getBondedDevices() : JSONArray {
        val array = JSONArray()
        for (d in adapter.bondedDevices) {
            val obj = JSONObject()
            obj.put("name", d.name)
            obj.put("address", d.address)
            obj.put("class", d.bluetoothClass.majorDeviceClass)
            array.put(obj)
        }
        return array
    }

    @JSBridgeInterface
    fun isAvailable(): Boolean {
        return adapter != null
    }

    @JSBridgeInterface
    fun isEnabled(): Boolean {
        return adapter?.isEnabled ?: false
    }

    @JSBridgeInterface
    fun enable(callback: JBCallback) {
        if (isEnabled()) {
            callback.resolve()
        } else {
            Application.registerReceiver(object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    when (intent?.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1)) {
                        BluetoothAdapter.STATE_ON -> {
                            Application.unregisterReceiver(this)
                            callback.resolve()
                        }
                    }
                }
            }, IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED))
            adapter?.enable()
        }
    }

    @JSBridgeInterface
    fun disable(callback: JBCallback) {
        if (!isEnabled()) {
            callback.resolve()
        } else {
            Application.registerReceiver(object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    when (intent?.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1)) {
                        BluetoothAdapter.STATE_OFF -> {
                            Application.unregisterReceiver(this)
                            callback.resolve()
                        }
                    }
                }
            }, IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED))
            adapter?.disable()
        }
    }
}
