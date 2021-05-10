package io.shinoharahare.jsbridge

import kotlinx.coroutines.*
import org.json.JSONObject
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.full.*

typealias DFunction = (args: JBArgument) -> Unit

abstract class JSBridgeModule {
    protected open val mName by lazy { this::class.simpleName }

    private val mFunctions: Collection<KFunction<*>> by lazy {
        this::class.memberFunctions.filter {
            it.findAnnotation<JSBridgeInterface>() != null
        }
    }

    private val mFunctionMap: Map<String, DFunction> by lazy {
        val map = mutableMapOf<String, DFunction>()
        for (function in mFunctions) {
            map[function.name] = decorate(function)
        }
        return@lazy map
    }

    val name get() = mName
    val functions get() = mFunctions

    private fun decorate(function: KFunction<*>) : DFunction {
        return {
            val args = it.toKParameter(this, function)
            val hasCallback = args.keys.any { it.type == JBCallback.Type }
            GlobalScope.launch {
                var result: Any? = null
                try {
                    result = if (function.isSuspend) {
                        function.callSuspendBy(args)
                    } else {
                        withContext(Dispatchers.Main) {
                            function.callBy(args)
                        }
                    }
                    if (!hasCallback) {
                        withContext(Dispatchers.Main) {
                            it.callback.resolve(result)
                        }
                    }
                } catch (e: Exception) {
                    if (!hasCallback) {
                        withContext(Dispatchers.Main) {
                            it.callback.reject(e)
                        }
                    }
                }
            }
        }
    }

    fun call(name: String, args: JBArgument) {
        val function = mFunctionMap[name]
        if (function == null) {
            args.callback.reject("Interface Not Found")
        } else {
            function(args)
        }
    }
}
