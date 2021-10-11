package com.keronei.domain.usecases

import com.keronei.domain.entities.MemberEntity
import com.keronei.domain.repository.MembersRepository
import com.keronei.domain.usecases.base.BaseUseCase

class DeleteMemberUseCase(private val membersRepository: MembersRepository) :
    BaseUseCase<MemberEntity, Unit> {
    override suspend fun invoke(params: MemberEntity) {
        return membersRepository.removeMemberFromRegister(params)
    }
}