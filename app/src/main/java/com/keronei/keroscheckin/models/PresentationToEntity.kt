package com.keronei.keroscheckin.models

import com.keronei.domain.entities.MemberEntity

    fun AttendeePresentation.toMemberEntity() : MemberEntity{
        return MemberEntity(memberId, firstName, secondName, otherNames, identificationNumber, sex, age,isMarried , phoneNumber, isActive, regionId)
    }
