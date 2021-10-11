package com.keronei.koregister.models

import com.keronei.domain.entities.AttendanceEntity

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