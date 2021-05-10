package io.shinoharahare.btlatch.JSModule

import io.shinoharahare.jsbridge.JSBridgeInterface
import io.shinoharahare.jsbridge.JSBridgeModule
import kotlin.system.exitProcess

object ApplicationBridge : JSBridgeModule() {

    @JSBridgeInterface
    fun exit() {
        exitProcess(0)
    }
}
