package com.keronei.koregister.models

import com.keronei.domain.entities.CheckInEntity

data class AttendeePresentation(
    val memberId : Int,
    val name: String,
    val age : Int,
    val phoneNumber : String?,
    val isActive : Boolean,
    val regionId : Int,
    val regionName : String,
    val checkIns: List<CheckInEntity>
)