package io.shinoharahare.jsbridge

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */


class Test {
    val bridge = JSBridge()

    @Test
    fun test() {
        bridge.addModule(Module)
    }
}

object Module : JSBridgeModule() {
    override val mName = "TestModule"

    @JSBridgeInterface
    fun func0() {
        println("func0 called")
    }

    @JSBridgeInterface
    fun func1(x: String) {
        println("func1 called: $x")
    }
}
