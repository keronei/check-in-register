package com.keronei.domain.usecases

import com.keronei.domain.entities.RegionEntity
import com.keronei.domain.repository.RegionsRepository
import com.keronei.domain.usecases.base.BaseUseCase

class DeleteRegionUseCase(private val regionsRepository: RegionsRepository) :
    BaseUseCase<RegionEntity, Int> {
    override suspend fun invoke(params: RegionEntity) : Int {
        return regionsRepository.deleteRegion(params)
    }
}