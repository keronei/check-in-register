package com.keronei.koregister.models

import com.keronei.domain.entities.AttendanceEntity
import com.keronei.domain.entities.CheckInEntity
import com.keronei.domain.entities.RegionEmbedEntity
import com.keronei.domain.entities.RegionEntity
import java.text.SimpleDateFormat
import java.util.*

fun AttendanceEntity.toPresentation(): AttendeePresentation {

    //latest checkIn has highest timestamp value

    val latestCheckIn = checkIns.maxByOrNull { entity -> entity.timeStamp }

    val currentTime = Calendar.getInstance()

    currentTime.set(Calendar.HOUR_OF_DAY, -8)

    val hasCheckIn = latestCheckIn?.timeStamp ?: 0L > currentTime.timeInMillis

    val parser = SimpleDateFormat("hh:mm a", Locale.US)

    return AttendeePresentation(
        memberEntity.firstName,
        memberEntity.secondName,
        memberEntity.otherNames,
        memberEntity.sex,
        memberEntity.id,
        memberEntity.firstName + " " + memberEntity.secondName + " " + memberEntity.otherNames,
        memberEntity.age,
        memberEntity.phoneNumber,
        memberEntity.isActive,
        regionEntity.id,
        regionEntity.name,
        if (hasCheckIn) parser.format(latestCheckIn!!.timeStamp) else "",
        latestCheckIn?.timeStamp
    )
}

fun RegionEmbedEntity.toPresentation(): RegionPresentation {
    return RegionPresentation(
        regionEntity.id.toString(),
        regionEntity.name,
        members.size.toString()
    )
}

fun CheckInEntity.toPresentation(): CheckInPresentation {
    return CheckInPresentation(id, memberId, timeStamp, temperature)
}

