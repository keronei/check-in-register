package com.keronei.data.local.embeds

import androidx.room.Embedded
import androidx.room.Relation
import com.keronei.data.local.entities.MemberDBO
import com.keronei.data.local.entities.RegionDBO

data class RegionEmbed (
    @Embedded val region : RegionDBO,

    @Relation(parentColumn = "id", entityColumn = "regionId")
    val membersDBO: List<MemberDBO>
)