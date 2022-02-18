package com.keronei.keroscheckin.di

import com.keronei.domain.repository.AttendanceDataRepository
import com.keronei.domain.repository.MembersRepository
import com.keronei.domain.usecases.AttendanceUseCases
import com.keronei.domain.usecases.CheckInMemberUseCase
import com.keronei.domain.usecases.ListAttendeesUseCase
import com.keronei.domain.usecases.UndoCheckInUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object CheckInUseCaseModule {
    @Provides
    fun providesCheckInMemberUseCase(
        checkInRepositoryImpl: AttendanceDataRepository,
        memberRepositoryImpl: MembersRepository
    ): AttendanceUseCases {
        return AttendanceUseCases(
            CheckInMemberUseCase(checkInRepositoryImpl),
            ListAttendeesUseCase(memberRepositoryImpl),
            UndoCheckInUseCase(checkInRepositoryImpl)
        )
    }
}