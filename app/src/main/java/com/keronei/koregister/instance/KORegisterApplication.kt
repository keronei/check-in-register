package com.keronei.koregister.instance

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class KORegisterApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: KORegisterApplication
            private set
    }

}