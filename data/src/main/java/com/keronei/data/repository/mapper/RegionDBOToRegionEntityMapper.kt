package com.keronei.data.repository.mapper

import com.keronei.data.local.entities.RegionDBO
import com.keronei.domain.common.Mapper
import com.keronei.domain.entities.RegionEntity

class RegionDBOToRegionEntityMapper : Mapper<RegionDBO, RegionEntity>() {
    override fun map(input: RegionDBO): RegionEntity {
        return RegionEntity(input.id, input.name)
    }
}