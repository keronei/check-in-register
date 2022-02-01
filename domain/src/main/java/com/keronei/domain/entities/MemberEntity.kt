package com.keronei.domain.entities

import java.lang.reflect.Field

data class MemberEntity(
    val id: Int,
    val firstName: String,
    val secondName: String,
    val otherNames: String,
    val sex: Int,
    val birthYear: Int,
    val phoneNumber: String?,
    val isActive: Boolean,
    val regionId: Int
) : BaseEntity() {
    override fun getValue(field: Field): Any {
        return when (field.name) {
            "id" -> id
            "firstName" -> firstName
            "secondName" -> secondName
            "otherNames" -> otherNames
            "sex" -> sex
            "birthYear" -> birthYear
            "phoneNumber" -> phoneNumber ?: ""
            "isActive" -> isActive
            "regionId" -> regionId
            else -> ""
        }

    }

    override fun toString(): String {
        return "$firstName $secondName $otherNames"
    }
}
