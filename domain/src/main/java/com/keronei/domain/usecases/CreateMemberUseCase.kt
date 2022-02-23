package com.keronei.domain.usecases

import com.keronei.domain.entities.MemberEntity
import com.keronei.domain.repository.MembersRepository
import com.keronei.domain.usecases.base.BaseUseCase


class CreateMemberUseCase(private val membersRepository: MembersRepository) :
    BaseUseCase<List<MemberEntity>, List<Long>> {
    override suspend fun invoke(params: List<MemberEntity>) : List<Long> {
        return membersRepository.addNewMember(params)
    }
}