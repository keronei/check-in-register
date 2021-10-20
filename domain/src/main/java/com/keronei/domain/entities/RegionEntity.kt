package com.keronei.domain.entities

data class RegionEntity(
    val id: Int,
    val name: String
) {
    override fun toString(): String {
        return name
    }
}