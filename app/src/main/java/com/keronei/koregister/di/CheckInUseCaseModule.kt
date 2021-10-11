package com.keronei.koregister.di

import com.keronei.data.repository.AttendanceDataRepositoryImpl
import com.keronei.data.repository.MembersRepositoryImpl
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
    fun providesCheckInMemberUseCase(checkInRepositoryImpl: AttendanceDataRepositoryImpl) : CheckInMemberUseCase{
        return CheckInMemberUseCase(checkInRepositoryImpl)
    }

    @Provides
    fun providesUndoCheckInMemberUseCase(attendanceDataRepositoryImpl: AttendanceDataRepositoryImpl) : UndoCheckInUseCase{
        return UndoCheckInUseCase(attendanceDataRepositoryImpl)
    }

    @Provides
    fun providesAttendeesListUseCase(attendanceDataRepositoryImpl: MembersRepositoryImpl) : ListAttendeesUseCase{
        return ListAttendeesUseCase(attendanceDataRepositoryImpl)
    }

}