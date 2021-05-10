package io.shinoharahare.jsbridge

import org.json.JSONObject
import java.lang.reflect.Type
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.createType
import kotlin.reflect.full.instanceParameter
import kotlin.reflect.full.valueParameters

class JBArgument(bridge: JSBridge, json: String) {
    private val mBridge = bridge
    private val mJSON = json
    private val mArguments = mutableMapOf<String, Any?>()
    private val mCallback: JBCallback

    val callback get() = mCallback

    init {
        val obj = JSONObject(json)
        mCallback = JBCallback(bridge, obj.getInt("_callback"))
        for (k in obj.keys()) {
            mArguments[k] = obj[k]
        }
    }

    fun get(name: String) : Any? {
        return mArguments[name]
    }

    fun toKParameter(instance: Any, function: KFunction<*>) : Map<KParameter, Any?> {
        val args = mutableMapOf<KParameter, Any?>()
        args[function.instanceParameter!!] = instance
        for (p in function.valueParameters) {
            if (p.type == JBCallback.Type) {
                args[p] = mCallback
            } else if (mArguments.contains(p.name)) {
                args[p] = mArguments[p.name]
            }
        }
        return args
    }
}
