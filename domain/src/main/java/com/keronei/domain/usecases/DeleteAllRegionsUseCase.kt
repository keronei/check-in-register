package com.keronei.domain.usecases

import com.keronei.domain.entities.RegionEntity
import com.keronei.domain.repository.RegionsRepository
import com.keronei.domain.usecases.base.BaseUseCase

class DeleteAllRegionsUseCase(private val regionsRepository: RegionsRepository ) :
    BaseUseCase<List<RegionEntity>, Int> {
    override suspend fun invoke(params: List<RegionEntity>): Int {
        return regionsRepository.deleteAllRegions(params)
    }
}