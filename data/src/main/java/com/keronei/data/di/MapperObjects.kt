package com.keronei.data.di

import com.keronei.data.repository.mapper.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object MapperObjects {

    @Provides
    fun providesCheckInEntity(): CheckInEntityLocalMapper = CheckInEntityLocalMapper()

    @Provides
    fun providesMemberDBOToEntity(): MemberDBOToEntityMapper = MemberDBOToEntityMapper()

    @Provides
    fun providesMemberLocalEntity(): MemberLocalEntityMapper = MemberLocalEntityMapper()

    @Provides
    fun providesCheckInDBOToEntity(): CheckInDBOToEntityMapper = CheckInDBOToEntityMapper()

    @Provides
    fun providesRegionDBOToRegionEntityMapper(): RegionDBOToRegionEntityMapper =
        RegionDBOToRegionEntityMapper()

    @Provides
    fun provideRegionEntityToRegionDBOMapper(): RegionEntityToRegionDBOMapper =
        RegionEntityToRegionDBOMapper()

    @Provides
    fun providesAttendanceEmbedToAttendanceEntityMapper(
        memberDBOToEntityMapper: MemberDBOToEntityMapper,
        checkInDBOToEntityMapper: CheckInDBOToEntityMapper,
        regionDBOToRegionEntityMapper: RegionDBOToRegionEntityMapper
    ): AttendanceEmbedToAttendanceEntityMapper = AttendanceEmbedToAttendanceEntityMapper(
        memberDBOToEntityMapper,
        checkInDBOToEntityMapper,
        regionDBOToRegionEntityMapper
    )

    @Provides
    fun providesRegionEmbedToRegionEmbedEntityMapper(
        regionDBOToRegionEntityMapper: RegionDBOToRegionEntityMapper,
        memberDBOToEntityMapper: MemberDBOToEntityMapper
    ): RegionEmbedToRegionEmbedEntityMapper {
        return RegionEmbedToRegionEmbedEntityMapper(
            regionDBOToRegionEntityMapper,
            memberDBOToEntityMapper
        )
    }

}