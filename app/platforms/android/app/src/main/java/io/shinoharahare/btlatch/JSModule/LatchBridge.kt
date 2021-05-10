package io.shinoharahare.btlatch.JSModule

import io.shinoharahare.btlatch.Communicator
import io.shinoharahare.jsbridge.JBCallback
import io.shinoharahare.jsbridge.JSBridgeInterface
import io.shinoharahare.jsbridge.JSBridgeModule
import org.json.JSONObject
import javax.security.auth.callback.Callback

object LatchBridge : JSBridgeModule() {
    @JSBridgeInterface
    suspend fun connect(address: String) : Boolean {
        return Communicator.connect(address)
    }

    @JSBridgeInterface
    suspend fun disconnect() {
        Communicator.disconnect()
    }

    @JSBridgeInterface
    fun connection() : Int {
        return Communicator.state.ordinal
    }

    @JSBridgeInterface
    suspend fun state(): Int {
        val res = Communicator.request("state")
        return res.getInt("data")
    }

    @JSBridgeInterface
    suspend fun lock() {
        Communicator.request("lock")
    }

    @JSBridgeInterface
    suspend fun unlock() {
        Communicator.request("unlock")
    }
}
