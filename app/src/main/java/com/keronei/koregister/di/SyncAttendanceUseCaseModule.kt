package com.keronei.koregister.di

import com.keronei.domain.usecases.SyncAttendanceUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object SyncAttendanceUseCaseModule {

    @Provides
    fun providesSyncAttendanceUseCase() : SyncAttendanceUseCase {
      return SyncAttendanceUseCase()
    }

}