package com.keronei.domain.usecases

import com.keronei.domain.entities.RegionEntity
import com.keronei.domain.repository.RegionsRepository
import com.keronei.domain.usecases.base.BaseUseCase
import com.keronei.domain.usecases.base.UseCaseParams
import kotlinx.coroutines.flow.Flow

class QueryAllRegionsUseCase(private val regionsRepository: RegionsRepository) :
    BaseUseCase<UseCaseParams.Empty, Flow<List<RegionEntity>>> {
    override suspend fun invoke(params: UseCaseParams.Empty): Flow<List<RegionEntity>> {
        return regionsRepository.getAllRegions()
    }
}