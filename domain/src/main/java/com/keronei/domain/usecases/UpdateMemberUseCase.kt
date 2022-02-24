package com.keronei.domain.usecases

import com.keronei.domain.entities.MemberEntity
import com.keronei.domain.repository.MembersRepository
import com.keronei.domain.usecases.base.BaseUseCase

class UpdateMemberUseCase(private val membersRepository: MembersRepository) :
    BaseUseCase<MemberEntity, Int> {
    override suspend fun invoke(params: MemberEntity) : Int {
        return membersRepository.updateMemberDetails(params)
    }

}