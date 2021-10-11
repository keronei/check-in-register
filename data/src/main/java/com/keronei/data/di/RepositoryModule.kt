package com.keronei.data.di

import com.keronei.data.local.dao.CheckInDao
import com.keronei.data.local.dao.MemberDao
import com.keronei.data.local.dao.RegionsDao
import com.keronei.data.repository.AttendanceDataRepositoryImpl
import com.keronei.data.repository.MembersRepositoryImpl
import com.keronei.data.repository.RegionsRepositoryImpl
import com.keronei.data.repository.mapper.*
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
    fun providesAttendanceRepository(
        attendanceDao: CheckInDao,
        checkInEntityLocalMapper: CheckInEntityLocalMapper
    ): AttendanceDataRepositoryImpl {

        return AttendanceDataRepositoryImpl(
            attendanceDao,
            checkInEntityLocalMapper
        )
    }

    @Provides
    fun providesRegionRepository(
        regionsDao: RegionsDao,
        memberDBOToEntityMapper: MemberDBOToEntityMapper,
        regionDBOToRegionEntityMapper: RegionDBOToRegionEntityMapper,
        regionEntityToRegionDBOMapper: RegionEntityToRegionDBOMapper,
    ): RegionsRepositoryImpl {
        return RegionsRepositoryImpl(
            regionsDao,
            regionDBOToRegionEntityMapper,
            regionEntityToRegionDBOMapper,
            memberDBOToEntityMapper,
        )
    }

    @Provides
    fun providesMemberRepository(
        memberDao: MemberDao,
        attendanceEmbedToAttendanceEntityMapper: AttendanceEmbedToAttendanceEntityMapper,
        memberDBOToEntityMapper: MemberDBOToEntityMapper,
        memberLocalEntityMapper: MemberLocalEntityMapper
    ): MembersRepositoryImpl {
        return MembersRepositoryImpl(
            memberDao,
            memberLocalEntityMapper,
            memberDBOToEntityMapper,
            attendanceEmbedToAttendanceEntityMapper,
        )
    }
}