package com.keronei.data.repository

import com.keronei.data.local.dao.MemberDao
import com.keronei.data.repository.mapper.MemberDBOToEntityMapper
import com.keronei.data.repository.mapper.MemberLocalEntityMapper
import com.keronei.domain.entities.MemberEntity
import com.keronei.domain.repository.MembersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MembersRepositoryImpl(
    private val memberDao: MemberDao,
    private val memberLocalEntityMapper: MemberLocalEntityMapper,
    private val memberDBOToEntityMapper: MemberDBOToEntityMapper
) : MembersRepository {
    override suspend fun addNewMember(memberEntity: MemberEntity) {
        memberDao.createNewMember(memberLocalEntityMapper.map(memberEntity))
    }

    override fun getAllMembers(): Flow<List<MemberEntity>> {
        return memberDao.getAllMembers()
            .map { memberDBO -> memberDBOToEntityMapper.mapList(memberDBO) }
    }

    override suspend fun updateMemberDetails(memberEntity: MemberEntity) {
        memberDao.updateMemberInformation(memberLocalEntityMapper.map(memberEntity))
    }

    override suspend fun removeMemberFromRegister(memberEntity: MemberEntity) {
        memberDao.deleteMember(memberLocalEntityMapper.map(memberEntity))
    }
}