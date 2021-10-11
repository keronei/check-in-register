package com.keronei.domain.usecases

import com.keronei.domain.entities.CheckInEntity
import com.keronei.domain.repository.AttendanceDataRepository
import com.keronei.domain.usecases.base.BaseUseCase

class UndoCheckInUseCase(private val attendanceDataRepository: AttendanceDataRepository) :
    BaseUseCase<CheckInEntity, Unit> {
    override suspend fun invoke(params: CheckInEntity) {
        return attendanceDataRepository.undoCheckingForMember(params)
    }
}