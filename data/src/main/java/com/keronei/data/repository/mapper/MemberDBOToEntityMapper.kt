package com.keronei.data.repository.mapper

import com.keronei.data.local.entities.MemberDBO
import com.keronei.domain.common.Mapper
import com.keronei.domain.entities.MemberEntity

class MemberDBOToEntityMapper : Mapper<MemberDBO, MemberEntity>() {
    override fun map(input: MemberDBO): MemberEntity {
        return MemberEntity(
            input.id,
            input.firstName,
            input.secondName,
            input.otherNames,
            input.sex,
            input.birthYear,
            input.phoneNumber,
            input.isActive,
            input.regionId
        )
    }
}