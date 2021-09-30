package com.keronei.data.di

import com.keronei.data.repository.mapper.CheckInEntityLocalMapper
import com.keronei.data.repository.mapper.MemberDBOToEntityMapper
import com.keronei.data.repository.mapper.MemberLocalEntityMapper
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

}