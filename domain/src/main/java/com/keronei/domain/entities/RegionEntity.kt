package com.keronei.domain.entities

import java.lang.reflect.Field

data class RegionEntity(
    val id: Int,
    val name: String
) : BaseEntity() {
    override fun getValue(field: Field): Any {
        return when (field.name) {
            "id" -> id
            "name" -> name
            else -> ""
        }
    }

    override fun toString(): String {
        return name
    }


}