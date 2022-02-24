package com.keronei.keroscheckin.di

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.keronei.keroscheckin.instance.KORegisterApplication
import com.keronei.keroscheckin.preference.DataStoreManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppInstance {
    @Provides
    @Singleton
    fun providesApplication(): KORegisterApplication {
        return KORegisterApplication()
    }

    @Provides
    @Singleton
    fun providesDataStoreInstance(@ApplicationContext context: Context): DataStoreManager {
        return DataStoreManager(context)
    }

    @Provides
    @Singleton
    fun providesPreferenceInstance(@ApplicationContext context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

}