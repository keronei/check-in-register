package com.keronei.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [ForeignKey(
        entity = MemberDBO::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("memberId"),
        onDelete = CASCADE
    )]
)
data class CheckInDBO(
    @PrimaryKey(autoGenerate = true)
    val checkInId: Int,

    @ColumnInfo(index = true)
    val memberId: Int,

    val timeStamp: Long,

    val temperature: Double
)
