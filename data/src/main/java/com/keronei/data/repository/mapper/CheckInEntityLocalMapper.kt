package com.keronei.data.repository.mapper

import com.keronei.data.local.entities.CheckInDBO
import com.keronei.domain.common.Mapper
import com.keronei.domain.entities.CheckInEntity

class CheckInEntityLocalMapper : Mapper<CheckInEntity, CheckInDBO>() {
    override fun map(input: CheckInEntity): CheckInDBO {
        return CheckInDBO(input.id, input.memberId, input.timeStamp, input.temperature)
    }
}