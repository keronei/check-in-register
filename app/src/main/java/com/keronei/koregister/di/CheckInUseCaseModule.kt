package com.keronei.koregister.di

import com.keronei.data.repository.AttendanceDataRepositoryImpl
import com.keronei.data.repository.MembersRepositoryImpl
import com.keronei.domain.repository.AttendanceDataRepository
import com.keronei.domain.repository.MembersRepository
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
    fun providesCheckInMemberUseCase(checkInRepositoryImpl: AttendanceDataRepository) : CheckInMemberUseCase{
        return CheckInMemberUseCase(checkInRepositoryImpl)
    }

    @Provides
    fun providesUndoCheckInMemberUseCase(attendanceDataRepositoryImpl: AttendanceDataRepository) : UndoCheckInUseCase{
        return UndoCheckInUseCase(attendanceDataRepositoryImpl)
    }

    @Provides
    fun providesAttendeesListUseCase(attendanceDataRepositoryImpl: MembersRepository) : ListAttendeesUseCase{
        return ListAttendeesUseCase(attendanceDataRepositoryImpl)
    }

}