package com.keronei.data.di

import android.content.Context
import com.keronei.data.local.KODatabase
import com.keronei.data.local.dao.CheckInDao
import com.keronei.data.local.dao.MemberDao
import com.keronei.data.local.dao.RegionsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun providesCoroutineScope() : CoroutineScope {
        return  CoroutineScope(SupervisorJob())
    }

    @Provides
    @Singleton
    fun providesDatabaseInstance(@ApplicationContext context: Context, scope: CoroutineScope ): KODatabase =
        KODatabase.buildDatabase(context, scope)

    @Provides
    @Singleton
    fun providesMemberDaoInstance(koDatabase: KODatabase): MemberDao = koDatabase.memberDao()

    @Provides
    @Singleton
    fun providesCheckInnDaoInstance(koDatabase: KODatabase): CheckInDao = koDatabase.checkInDao()

    @Provides
    @Singleton
    fun providesRegionDao(koDatabase: KODatabase): RegionsDao = koDatabase.regionDao()
}