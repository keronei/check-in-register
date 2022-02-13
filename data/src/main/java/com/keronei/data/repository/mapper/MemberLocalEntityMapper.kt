package com.keronei.data.repository.mapper

import com.keronei.data.local.entities.MemberDBO
import com.keronei.domain.common.Mapper
import com.keronei.domain.entities.MemberEntity

class MemberLocalEntityMapper : Mapper<MemberEntity, MemberDBO>() {
    override fun map(input: MemberEntity): MemberDBO {
        return MemberDBO(
            input.id,
            input.firstName,
            input.secondName,
            input.otherNames,
            input.sex,
            input.birthYear,
            input.isMarried,
            input.phoneNumber,
            input.isActive,
            input.regionId
        )
    }
}