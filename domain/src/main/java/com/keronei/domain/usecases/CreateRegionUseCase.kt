package com.keronei.domain.usecases

import com.keronei.domain.entities.RegionEntity
import com.keronei.domain.repository.RegionsRepository
import com.keronei.domain.usecases.base.BaseUseCase

class CreateRegionUseCase(private val regionsRepository: RegionsRepository) :
    BaseUseCase<RegionEntity, Long> {
    override suspend fun invoke(params: RegionEntity): Long {
        return regionsRepository.createNewRegion(params)
    }
}