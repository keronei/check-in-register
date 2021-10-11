package com.keronei.domain.usecases.params

import com.keronei.domain.entities.CheckInEntity
import com.keronei.domain.entities.MemberEntity
import com.keronei.domain.usecases.base.UseCaseParams

data class CheckInParam(val checkInEntity: CheckInEntity, val memberEntity: MemberEntity) :
    UseCaseParams