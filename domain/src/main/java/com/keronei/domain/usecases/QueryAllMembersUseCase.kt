package com.keronei.domain.usecases

import com.keronei.domain.entities.MemberEntity
import com.keronei.domain.repository.MembersRepository
import com.keronei.domain.usecases.base.BaseUseCase
import com.keronei.domain.usecases.base.UseCaseParams
import kotlinx.coroutines.flow.Flow

class QueryAllMembersUseCase(private val membersRepository: MembersRepository) :
    BaseUseCase<UseCaseParams.Empty, Flow<List<MemberEntity>>> {
    override suspend fun invoke(params: UseCaseParams.Empty): Flow<List<MemberEntity>> {
        return membersRepository.getAllMembers()
    }
}