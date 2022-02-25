package com.keronei.keroscheckin.models

import com.keronei.domain.entities.MemberEntity
import java.util.*

fun AttendeePresentation.toMemberEntity(): MemberEntity {
    val currentYear = Calendar.getInstance()[Calendar.YEAR]
    return MemberEntity(
        memberId,
        firstName,
        secondName,
        otherNames,
        identificationNumber,
        sex,
        (currentYear - age),
        isMarried,
        phoneNumber,
        isActive,
        regionId
    )
}
