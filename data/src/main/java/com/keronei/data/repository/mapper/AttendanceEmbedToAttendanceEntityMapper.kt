package com.keronei.data.repository.mapper

import com.keronei.data.local.embeds.Attendance
import com.keronei.domain.common.Mapper
import com.keronei.domain.entities.AttendanceEntity

class AttendanceEmbedToAttendanceEntityMapper(
    private val memberDBOToEntityMapper: MemberDBOToEntityMapper,
    private val checkInDBOToEntityMapper: CheckInDBOToEntityMapper,
    private val regionDBOMapper: RegionDBOToRegionEntityMapper
) : Mapper<Attendance, AttendanceEntity>() {
    override fun map(input: Attendance): AttendanceEntity {
        return AttendanceEntity(
            memberDBOToEntityMapper.map(input.memberDBO),
            regionDBOMapper.map(input.locality),
            checkInDBOToEntityMapper.mapList(input.checkIns)
        )
    }
}