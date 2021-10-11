package com.keronei.domain.usecases

import com.keronei.domain.repository.AttendanceDataRepository
import com.keronei.domain.usecases.base.BaseUseCase
import com.keronei.domain.usecases.params.CheckInParam

class CheckInMemberUseCase(private val attendanceDataRepository: AttendanceDataRepository) :
    BaseUseCase<CheckInParam, Unit> {
    override suspend fun invoke(params: CheckInParam) {
        return attendanceDataRepository.checkInMember(params.memberEntity, params.checkInEntity)
    }
}