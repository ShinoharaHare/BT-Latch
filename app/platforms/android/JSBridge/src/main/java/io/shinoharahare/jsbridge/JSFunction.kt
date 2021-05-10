package io.shinoharahare.jsbridge

import android.webkit.WebView

class JSFunction(webView: WebView , location: String) {
    private val mWebView = webView
    private val mLocation = location

    fun invoke(vararg args: Any) {
        mWebView.evaluateJavascript("$mLocation();") {
            print(it)
        }
    }
}
