package com.keronei.data.repository

import com.keronei.data.local.dao.MemberDao
import com.keronei.data.repository.mapper.AttendanceEmbedToAttendanceEntityMapper
import com.keronei.data.repository.mapper.MemberDBOToEntityMapper
import com.keronei.data.repository.mapper.MemberLocalEntityMapper
import com.keronei.domain.entities.AttendanceEntity
import com.keronei.domain.entities.MemberEntity
import com.keronei.domain.repository.MembersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MembersRepositoryImpl(
    private val memberDao: MemberDao,
    private val memberLocalEntityMapper: MemberLocalEntityMapper,
    private val memberDBOToEntityMapper: MemberDBOToEntityMapper,
    private val attendanceEmbedToAttendanceEntityMapper: AttendanceEmbedToAttendanceEntityMapper
) : MembersRepository {
    override suspend fun addNewMember(memberEntity: List<MemberEntity>) : List<Long> {
       return memberDao.createNewMember(memberLocalEntityMapper.mapList(memberEntity))
    }

    override fun getAllMembers(): Flow<List<MemberEntity>> {
        return memberDao.getAllMembers()
            .map { memberDBO -> memberDBOToEntityMapper.mapList(memberDBO) }
    }

    override suspend fun updateMemberDetails(memberEntity: MemberEntity) : List<Long> {
        return memberDao.updateMemberInformation(memberLocalEntityMapper.map(memberEntity))
    }

    override suspend fun removeMemberFromRegister(memberEntity: MemberEntity) : Int {
       return memberDao.deleteMember(memberLocalEntityMapper.map(memberEntity))
    }

    override suspend fun getAllAttendanceData(): Flow<List<AttendanceEntity>> {
        return memberDao.getAttendanceInformation()
            .map { attendanceData -> attendanceEmbedToAttendanceEntityMapper.mapList(attendanceData) }
    }

    override suspend fun deleteAllMembers(): Int {
        return try {
            memberDao.deleteAllMembers()
        } catch (exception: Exception) {
            exception.printStackTrace()
            0
        }
    }
}