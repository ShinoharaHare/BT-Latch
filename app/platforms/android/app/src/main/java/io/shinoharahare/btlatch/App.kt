package io.shinoharahare.btlatch

import android.app.Application
import android.content.Context
import kotlin.properties.Delegates

class App : Application() {
  companion object {
    private var _instance: Application by Delegates.notNull()

    val instance get() = _instance
    val context: Context get() = _instance.applicationContext
  }

  override fun onCreate() {
    super.onCreate()
    _instance = this
  }
}

val Application = App.instance
