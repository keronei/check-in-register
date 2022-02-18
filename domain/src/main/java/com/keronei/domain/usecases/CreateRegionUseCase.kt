package com.keronei.domain.usecases

import com.keronei.domain.entities.RegionEntity
import com.keronei.domain.repository.RegionsRepository
import com.keronei.domain.usecases.base.BaseUseCase

class CreateRegionUseCase(private val regionsRepository: RegionsRepository) :
    BaseUseCase<List<RegionEntity>, Unit> {
    override suspend fun invoke(params: List<RegionEntity>) {
        return regionsRepository.createNewRegion(params)
    }
}