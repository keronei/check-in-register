package com.keronei.data.repository

import com.keronei.data.local.dao.CheckInDao
import com.keronei.data.local.dao.MemberDao
import com.keronei.data.repository.mapper.CheckInEntityLocalMapper
import com.keronei.data.repository.mapper.MemberDBOToEntityMapper
import com.keronei.data.repository.mapper.MemberLocalEntityMapper
import com.keronei.domain.entities.CheckInEntity
import com.keronei.domain.entities.MemberEntity
import com.keronei.domain.entities.SyncSuccessEntity
import com.keronei.domain.entities.UserEntity
import com.keronei.domain.exception.Failure
import com.keronei.domain.repository.AttendanceDataRepository
import com.keronei.domain.usecases.base.Either
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AttendanceDataRepositoryImpl(
    private val memberDao: MemberDao,
    private val attendanceDao: CheckInDao,
    private val memberLocalEntityMapper: MemberLocalEntityMapper,
    private val checkInEntityLocalMapper: CheckInEntityLocalMapper,
    private val memberDBOToEntityMapper: MemberDBOToEntityMapper
) : AttendanceDataRepository {
    override suspend fun addNewMember(memberEntity: MemberEntity): Either<Failure, MemberEntity> {
        //TODO actualize return value
        memberDao.createNewMember(
            memberDBO = memberLocalEntityMapper.map(
                memberEntity
            )
        )

        return Either.Success(
            memberEntity
        )
    }

    override suspend fun sendAttendanceData(): Either<Failure, SyncSuccessEntity> {
        TODO("Not yet implemented")
    }

    override fun getAllMembers(): Flow<List<MemberEntity>> =
        memberDao.getAllMembers().map { input ->
            memberDBOToEntityMapper.mapList(input)

        }

    override suspend fun updateMemberDetails(memberEntity: MemberEntity): Either<Failure, MemberEntity> {

        memberDao.updateMemberInformation(
            memberDBO = memberLocalEntityMapper.map(
                memberEntity
            )
        )

        return Either.Success(
            memberEntity
        )
    }

    override suspend fun removeMemberFromRegister(memberEntity: MemberEntity): Either<Failure, MemberEntity> {
        memberDao.deleteMember(memberLocalEntityMapper.map(memberEntity))

        return Either.Success(memberEntity)
    }

    override suspend fun checkInMember(
        memberEntity: MemberEntity,
        checkInEntity: CheckInEntity
    ): Either<Failure, MemberEntity> {

        attendanceDao.checkInMember(checkInEntityLocalMapper.map(checkInEntity))

        return Either.Success(memberEntity)
    }

    override suspend fun undoCheckingForMember(checkInEntity: CheckInEntity): Either<Failure, CheckInEntity> {
        attendanceDao.cancelChecking(checkInEntityLocalMapper.map(checkInEntity))

        return Either.Success(checkInEntity)
    }

    override suspend fun authenticateUser(
        phoneNumber: String,
        password: String
    ): Either<Failure, UserEntity> {
        TODO("Not yet implemented")
    }
}