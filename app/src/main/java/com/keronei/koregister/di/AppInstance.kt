package com.keronei.koregister.di

import com.keronei.koregister.instance.KORegisterApplication
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
    fun providesApplication() : KORegisterApplication{
        return KORegisterApplication()
    }
}