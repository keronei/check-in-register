package com.keronei.domain.entities

data class MemberEntity(
    val id: Int,
    val firstName: String,
    val secondName: String,
    val otherNames: String,
    val sex : Int,
    val age: Int,
    val phoneNumber: String?,
    val isActive: Boolean,
    val regionId: Int
)
