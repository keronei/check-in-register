package com.keronei.domain.entities

data class CheckInEntity(
    val id : Int,
    val memberId: Int,
    val timeStamp: Long,
    val temperature: Double
)
