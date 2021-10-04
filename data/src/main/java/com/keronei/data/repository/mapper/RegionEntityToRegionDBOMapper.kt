package com.keronei.data.repository.mapper

import com.keronei.data.local.entities.RegionDBO
import com.keronei.domain.common.Mapper
import com.keronei.domain.entities.RegionEntity

class RegionEntityToRegionDBOMapper : Mapper<RegionEntity, RegionDBO>() {
    override fun map(input: RegionEntity): RegionDBO {
        return RegionDBO(input.id, input.name)
    }
}