package com.keronei.domain.repository

import com.keronei.domain.entities.CheckInEntity
import com.keronei.domain.entities.MemberEntity
import com.keronei.domain.entities.SyncSuccessEntity
import com.keronei.domain.entities.UserEntity
import com.keronei.domain.exception.Failure
import com.keronei.domain.usecases.base.Either

interface AttendanceDataRepository {

    suspend fun sendAttendanceData(): Either<Failure, SyncSuccessEntity>

    suspend fun checkInMember(
        memberEntity: MemberEntity,
        checkInEntity: CheckInEntity
    )

    suspend fun undoCheckingForMember(checkInEntity: CheckInEntity)

    suspend fun authenticateUser(phoneNumber: String, password: String): Either<Failure, UserEntity>

}