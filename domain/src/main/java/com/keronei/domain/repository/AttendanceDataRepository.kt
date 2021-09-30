package com.keronei.domain.repository

import com.keronei.domain.entities.CheckInEntity
import com.keronei.domain.entities.MemberEntity
import com.keronei.domain.entities.SyncSuccessEntity
import com.keronei.domain.entities.UserEntity
import com.keronei.domain.exception.Failure
import com.keronei.domain.usecases.base.Either
import kotlinx.coroutines.flow.Flow

interface AttendanceDataRepository {
    suspend fun addNewMember(memberEntity: MemberEntity) : Either<Failure, MemberEntity>

    suspend fun sendAttendanceData(): Either<Failure, SyncSuccessEntity>

    fun getAllMembers(): Flow<List<MemberEntity>>

    suspend fun updateMemberDetails(memberEntity: MemberEntity): Either<Failure, MemberEntity>

    suspend fun removeMemberFromRegister(memberEntity: MemberEntity): Either<Failure, MemberEntity>

    suspend fun checkInMember(
        memberEntity: MemberEntity,
        checkInEntity: CheckInEntity
    ): Either<Failure, MemberEntity>

    suspend fun undoCheckingForMember(checkInEntity: CheckInEntity) : Either<Failure, CheckInEntity>

    suspend fun authenticateUser(phoneNumber: String, password: String): Either<Failure, UserEntity>

}