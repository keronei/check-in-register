package com.keronei.domain.usecases

import com.keronei.domain.entities.RegionEmbedEntity
import com.keronei.domain.repository.RegionsRepository
import com.keronei.domain.usecases.base.BaseUseCase
import com.keronei.domain.usecases.base.UseCaseParams
import kotlinx.coroutines.flow.Flow

class QueryAllRegionsWithMembersUseCase(private val regionsRepository: RegionsRepository) :
    BaseUseCase<UseCaseParams.Empty, Flow<List<RegionEmbedEntity>>> {
    override suspend fun invoke(params: UseCaseParams.Empty): Flow<List<RegionEmbedEntity>> {
        return regionsRepository.getAllRegionsWithMembersData()
    }
}