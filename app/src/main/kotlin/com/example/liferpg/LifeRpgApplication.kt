package com.example.liferpg

import android.app.Application
import com.example.liferpg.di.AppContainer

class LifeRpgApplication : Application() {
  lateinit var container: AppContainer
    private set

  override fun onCreate() {
    super.onCreate()
    container = AppContainer(this)
  }
}
