package com.keronei.domain.usecases

import com.keronei.domain.entities.RegionEntity
import com.keronei.domain.repository.RegionsRepository
import com.keronei.domain.usecases.base.BaseUseCase

class DeleteAllRegionsUseCase(private val regionsRepository: RegionsRepository) :
    BaseUseCase<Unit, Int> {
    override suspend fun invoke(params: Unit): Int {
        return regionsRepository.deleteAllRegions()
    }
}