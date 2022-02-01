package com.keronei.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class RegionDBO(
    @PrimaryKey(autoGenerate = true)
    val id: Int,

    val name: String
)