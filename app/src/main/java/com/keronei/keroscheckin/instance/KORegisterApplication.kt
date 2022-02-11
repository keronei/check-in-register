package com.keronei.keroscheckin.instance

import android.app.Application
import com.keronei.keroscheckin.BuildConfig
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class KORegisterApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this

        if(BuildConfig.DEBUG){
            Timber.plant(Timber.DebugTree())
        }
    }

    companion object {
        lateinit var instance: KORegisterApplication
            private set
    }

}