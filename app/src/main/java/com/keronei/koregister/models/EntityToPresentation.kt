package com.keronei.koregister.models

import com.keronei.domain.entities.AttendanceEntity
import com.keronei.domain.entities.RegionEmbedEntity
import com.keronei.domain.entities.RegionEntity

fun AttendanceEntity.toPresentation(): AttendeePresentation {
    return AttendeePresentation(
        memberEntity.id,
        memberEntity.firstName + " " + memberEntity.secondName + " " + memberEntity.otherNames,
        memberEntity.age,
        memberEntity.phoneNumber,
        memberEntity.isActive,
        regionEntity.id,
        regionEntity.name,
        checkIns
    )
}

fun RegionEmbedEntity.toPresentation(): RegionPresentation {
    return RegionPresentation(regionEntity.id.toString(), regionEntity.name, members.size.toString())
}