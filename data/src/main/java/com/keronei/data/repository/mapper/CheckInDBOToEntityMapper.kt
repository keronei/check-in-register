package com.keronei.data.repository.mapper

import com.keronei.data.local.entities.CheckInDBO
import com.keronei.domain.common.Mapper
import com.keronei.domain.entities.CheckInEntity

class CheckInDBOToEntityMapper : Mapper<CheckInDBO, CheckInEntity>() {
    override fun map(input: CheckInDBO): CheckInEntity {
        return CheckInEntity(input.checkInId, input.memberId, input.timeStamp, input.temperature)
    }
}