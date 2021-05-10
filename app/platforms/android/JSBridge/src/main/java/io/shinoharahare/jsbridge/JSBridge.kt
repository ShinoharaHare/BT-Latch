package io.shinoharahare.jsbridge

import android.annotation.SuppressLint
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import org.json.JSONObject
import kotlin.reflect.full.valueParameters


class JSBridge {
    private lateinit var mWebView: WebView
    private val mModuleMap: MutableMap<String, JSBridgeModule> = mutableMapOf()
    private val mInjectionName = "${'$'}native"
    private var mReady = false

    val injectionName get() = mInjectionName
    val webView get() = mWebView
    val ready get() = mReady

    @SuppressLint("SetJavaScriptEnabled")
    fun setup(webView: WebView) {
        mWebView = webView
        mWebView.settings.javaScriptEnabled = true
        mWebView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)

                view?.evaluateJavascript("""
                    if (!window.hasOwnProperty('$injectionName')) {
                        ${injectionCode()}
                        window.dispatchEvent(new Event('nativeReady'))
                    }
                """.trimIndent()) {}

                mReady = true
            }

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                if (request?.url?.scheme == "native") {
                    val module = request.url.getQueryParameter("module")
                    val function = request.url.getQueryParameter("function")
                    val args = request.url.getQueryParameter("args")
                    call(module!!, function!!, args!!)
                    return true
                }
                return super.shouldOverrideUrlLoading(view, request)
            }
        }
    }

    private fun coreInjectionCode() : String {
        return """
            window.$mInjectionName = {
                get platform() { return 'android' },
                _functions: {},
//                _events: {
//                    map: new Map(),
//                    on(event, handler) {
//                        let handlers = this.map[event] || []
//                        handlers.push(handler)
//                        this.map[event] = handlers
//                    },
//                    emit(event, data) {
//                        let handlers = this.map[event] || []
//                        for (let handler of handlers) {
//                            handler({ type: event, data })
//                        }
//                    }
//                },
                _callbacks: {
                    _index: 0,
                    put(resolve, reject) {
                        let i = this._index++
                        let self = this
                        this[i] = {
                            resolve(...args) {
                                resolve(...args)
                                delete self[i]
                            },
                            reject(...args) {
                                reject(...args)
                                delete self[i]
                            }
                        }
                        return i
                    }
                },
                _call(mName, fName, args) {
                    args = args || {}
                    return new Promise((resolve, reject) => {
                        let id = this._callbacks.put(resolve, reject)
                        args._callback = `${'$'}{id}`
                        location.href = `native://?module=${'$'}{mName}&function=${'$'}{fName}&args=${'$'}{JSON.stringify(args)}`
                    })
                },
                on(event, callback) {
                    return window.addEventListener(`__native__${'$'}{event}`, callback)
//                    return this._events.on(event, handler)
                },
                _emit(event, detail) {
                    return window.dispatchEvent(new CustomEvent(`__native__${'$'}{event}`, { detail }))
                }
            };
        """.trimIndent()
    }

    private fun moduleInjectionCode() : String {
        var js = ""
        for (entry in mModuleMap) {
            js += "window.$mInjectionName.${entry.key} = {"
            for (f in entry.value.functions) {
                var parameters = ""
                for (p in f.valueParameters) {
                    if (p.type != JBCallback.Type) {
                        parameters += "${p.name}, "
                    }
                }
                js += "\n"
                js += """
                ${f.name}($parameters) {
                    return window.$mInjectionName._call('${entry.key}', '${f.name}', { $parameters })
                },
                """.trimIndent()
            }
            js += "\n};\n"
        }
        return js
    }

    fun injectionCode() : String {
        return coreInjectionCode() + moduleInjectionCode()
    }

    fun addModule(module: JSBridgeModule) {
        if (module.name != null) {
            mModuleMap[module.name!!] = module
        } else {
            throw Exception("Module Name Must Be Provided")
        }
    }

    fun addModule(name: String, module: JSBridgeModule) {
        mModuleMap[name] = module
    }

    fun call(mName: String, fName: String, json: String) {
        val module = mModuleMap[mName]
        val args = JBArgument(this, json)
        if (module == null) {
            args.callback.reject("Module Not Found")
        } else {
            module.call(fName, args)
        }
    }

    fun emit(event: String, detail: Any? = null) {
        val script =
            when (detail) {
                is Number, is Boolean -> "$detail"
                is JSONObject -> "JSON.parse($detail)"
                else -> "'$detail'"
            }

        mWebView.evaluateJavascript("""
            window.${'$'}native._emit('$event', $script)
        """.trimIndent()) {}
    }
}
