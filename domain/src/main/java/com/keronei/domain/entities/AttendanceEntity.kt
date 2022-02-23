package com.keronei.domain.entities



data class AttendanceEntity(
    val memberEntity: MemberEntity,
    val regionEntity: RegionEntity,
    val checkIns: List<CheckInEntity>
)
