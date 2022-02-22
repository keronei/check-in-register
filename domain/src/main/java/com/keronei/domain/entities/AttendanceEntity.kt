package com.keronei.domain.entities


@Parcelize
data class AttendanceEntity(
    val memberEntity: MemberEntity,
    val regionEntity: RegionEntity,
    val checkIns: List<CheckInEntity>
) : Parcelable
