package com.keronei.domain.usecases

import com.keronei.domain.entities.AttendanceEntity
import com.keronei.domain.models.AttendeeModel
import com.keronei.domain.repository.AttendanceDataRepository
import com.keronei.domain.repository.MembersRepository
import com.keronei.domain.usecases.base.BaseUseCase
import com.minor.mvvm_clean_architecture.domain.usecases.base.UseCaseParams
import kotlinx.coroutines.flow.Flow

class ListAttendeesUseCase(private val attendanceDataRepository: MembersRepository) :
    BaseUseCase<UseCaseParams.Empty, Flow<List<AttendanceEntity>>> {
    override suspend fun invoke(params: UseCaseParams.Empty): Flow<List<AttendanceEntity>> {
        return attendanceDataRepository.getAllAttendanceData()
    }
}