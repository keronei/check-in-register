package com.keronei.data.repository

import com.keronei.data.local.dao.CheckInDao
import com.keronei.data.repository.mapper.CheckInEntityLocalMapper
import com.keronei.domain.entities.CheckInEntity
import com.keronei.domain.entities.MemberEntity
import com.keronei.domain.entities.SyncSuccessEntity
import com.keronei.domain.entities.UserEntity
import com.keronei.domain.exception.Failure
import com.keronei.domain.repository.AttendanceDataRepository
import com.keronei.domain.usecases.base.Either

class AttendanceDataRepositoryImpl(
    private val attendanceDao: CheckInDao,
    private val checkInEntityLocalMapper: CheckInEntityLocalMapper
) : AttendanceDataRepository {

    override suspend fun sendAttendanceData(): Either<Failure, SyncSuccessEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun checkInMember(memberEntity: MemberEntity, checkInEntity: CheckInEntity) {
        attendanceDao.checkInMember(checkInEntityLocalMapper.map(checkInEntity))
    }

    override suspend fun undoCheckingForMember(checkInEntity: CheckInEntity) {
        attendanceDao.cancelChecking(checkInEntityLocalMapper.map(checkInEntity))
    }

    override suspend fun authenticateUser(
        phoneNumber: String,
        password: String
    ): Either<Failure, UserEntity> {
        TODO("Not yet implemented")
    }
}