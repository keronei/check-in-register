package com.keronei.domain.repository

import com.keronei.domain.entities.AttendanceEntity
import com.keronei.domain.entities.MemberEntity
import com.keronei.domain.exception.Failure
import com.keronei.domain.usecases.base.Either
import kotlinx.coroutines.flow.Flow

interface MembersRepository {
    suspend fun addNewMember(memberEntity: List<MemberEntity>)

    fun getAllMembers(): Flow<List<MemberEntity>>

    suspend fun updateMemberDetails(memberEntity: MemberEntity)

    suspend fun removeMemberFromRegister(memberEntity: MemberEntity)

    suspend fun getAllAttendanceData(): Flow<List<AttendanceEntity>>

    suspend fun deleteAllMembers() : Int

}