package io.shinoharahare.jsbridge

import kotlin.reflect.full.createType


class JBCallback(bridge: JSBridge, id: Int) {
    companion object {
        val Type = JBCallback::class.createType()
    }

    private val mBridge = bridge
    private val mID = id

    private fun argsToJson(vararg args: Any?) : String {
        var result = JSONAble.from(args)
        result = result.substring(1, result.length - 1)
        return result
    }

    fun resolve(vararg args: Any?) {
        mBridge.webView.evaluateJavascript("""
            window.${mBridge.injectionName}._callbacks[$mID].resolve(${argsToJson(*args)})
        """.trimIndent()) {}
    }

    fun reject(vararg args: Any?) {
        mBridge.webView.evaluateJavascript("""
            window.${mBridge.injectionName}._callbacks[$mID].reject(${argsToJson(*args)})
        """.trimIndent()) {}
    }
}
