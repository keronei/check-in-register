package com.keronei.domain.usecases

import com.keronei.domain.repository.MembersRepository
import com.keronei.domain.usecases.base.BaseUseCase
import com.keronei.domain.usecases.base.UseCaseParams

class DeleteAllMembersUseCase(private val membersRepository: MembersRepository) :
    BaseUseCase<Unit, Int> {
    override suspend fun invoke(params: Unit): Int {
        return membersRepository.deleteAllMembers()
    }
}