package com.keronei.data.repository.mapper

import com.keronei.data.local.embeds.RegionEmbed
import com.keronei.domain.common.Mapper
import com.keronei.domain.entities.RegionEmbedEntity

class RegionEmbedToRegionEmbedEntityMapper(
    private val regionDBOToRegionEntityMapper: RegionDBOToRegionEntityMapper,
    private val memberDBOToEntityMapper: MemberDBOToEntityMapper
) : Mapper<RegionEmbed, RegionEmbedEntity>() {
    override fun map(input: RegionEmbed): RegionEmbedEntity {
        return RegionEmbedEntity(
            regionDBOToRegionEntityMapper.map(input.region),
            memberDBOToEntityMapper.mapList(input.membersDBO)
        )
    }
}