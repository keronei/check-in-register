package com.keronei.domain.entities

import java.lang.reflect.Field

data class MemberEntity(
    var id: Int,
    var firstName: String,
    var secondName: String,
    var otherNames: String,
    var identificationNumber : String?,
    var sex: Int,
    var birthYear: Int,
    var isMarried : Boolean,
    var phoneNumber: String?,
    var isActive: Boolean,
    var regionId: Int
) : BaseEntity() {
    override fun getValue(field: Field): Any {
        return when (field.name) {
            "id" -> id
            "firstName" -> firstName
            "secondName" -> secondName
            "otherNames" -> otherNames
            "identificationNumber" -> identificationNumber ?: ""
            "sex" -> sex
            "birthYear" -> birthYear
            "isMarried" -> isMarried
            "phoneNumber" -> phoneNumber ?: ""
            "isActive" -> isActive
            "regionId" -> regionId
            else -> ""
        }

    }

}
