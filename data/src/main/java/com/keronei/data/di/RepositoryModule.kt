package com.keronei.data.di

import com.keronei.data.local.dao.CheckInDao
import com.keronei.data.repository.AttendanceDataRepositoryImpl
import com.keronei.data.repository.mapper.CheckInEntityLocalMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun providesMemberRepository(
        attendanceDao: CheckInDao,
        checkInEntityLocalMapper: CheckInEntityLocalMapper
    ): AttendanceDataRepositoryImpl {

        return AttendanceDataRepositoryImpl(
            attendanceDao,
            checkInEntityLocalMapper
        )
    }
}