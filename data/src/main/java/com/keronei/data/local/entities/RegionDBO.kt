package com.keronei.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.lang.reflect.Field


@Entity
data class RegionDBO(
    @PrimaryKey(autoGenerate = true)
    val id: Int,

    val name: String
) : BaseDBO() {
    override fun getValue(field: Field): Any {

        return when (field.name) {
            "id" -> this.id
            "name" -> this.name
            else -> ""
        }

    }

}