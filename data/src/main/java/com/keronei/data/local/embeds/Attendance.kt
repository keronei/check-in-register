package com.keronei.data.local.embeds

import androidx.room.Embedded
import androidx.room.Relation
import com.keronei.data.local.entities.CheckInDBO
import com.keronei.data.local.entities.MemberDBO
import com.keronei.data.local.entities.RegionDBO

data class Attendance(
    @Embedded val memberDBO: MemberDBO,

    @Relation(parentColumn = "id", entityColumn = "memberId")
    val checkIns: List<CheckInDBO>,

    @Relation(parentColumn = "regionId", entityColumn = "id")
    val locality : RegionDBO
)