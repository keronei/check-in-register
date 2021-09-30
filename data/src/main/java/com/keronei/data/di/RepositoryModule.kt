package com.keronei.data.di

import com.keronei.data.local.dao.CheckInDao
import com.keronei.data.local.dao.MemberDao
import com.keronei.data.repository.AttendanceDataRepositoryImpl
import com.keronei.data.repository.mapper.CheckInEntityLocalMapper
import com.keronei.data.repository.mapper.MemberDBOToEntityMapper
import com.keronei.data.repository.mapper.MemberLocalEntityMapper
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
        checkInEntityLocalMapper: CheckInEntityLocalMapper,
        memberDBOToEntityMapper: MemberDBOToEntityMapper,
        memberDao: MemberDao,
        memberLocalEntityMapper: MemberLocalEntityMapper
    ): AttendanceDataRepositoryImpl {

        return AttendanceDataRepositoryImpl(
            memberDao,
            attendanceDao,
            memberLocalEntityMapper,
            checkInEntityLocalMapper,
            memberDBOToEntityMapper
        )
    }
}