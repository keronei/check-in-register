package com.keronei.keroscheckin.instance

import android.app.Application
import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.keronei.keroscheckin.BuildConfig
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class KORegisterApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this

        plantTimberTree()
    }

    private fun plantTimberTree() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(FirebaseCrashlyticsReportingTree())
        }
    }

    private class FirebaseCrashlyticsReportingTree : Timber.Tree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG) return

            val throwable = t ?: Exception(message)

            FirebaseCrashlytics.getInstance().apply {
                setCustomKey(CRASHLYTICS_KEY_PRIORITY, priority)
                setCustomKey(CRASHLYTICS_KEY_MESSAGE, message)
                setCustomKey(CRASHLYTICS_KEY_TAG, tag ?: "Unspecified tag.")
            }.also {
                it.recordException(throwable)
            }
        }

        companion object {
            private const val CRASHLYTICS_KEY_PRIORITY = "priority"
            private const val CRASHLYTICS_KEY_TAG = "tag"
            private const val CRASHLYTICS_KEY_MESSAGE = "message"
        }

    }

    companion object {
        lateinit var instance: KORegisterApplication
            private set
    }

}