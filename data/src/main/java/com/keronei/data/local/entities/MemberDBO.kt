package com.keronei.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.RESTRICT
import androidx.room.PrimaryKey
import java.lang.reflect.Field

@Entity(
    foreignKeys = [ForeignKey(
        entity = RegionDBO::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("regionId"),
        onDelete = RESTRICT
    )]
)
data class MemberDBO(
    @PrimaryKey(autoGenerate = true)
    val id: Int,

    val firstName: String,

    val secondName: String,

    val otherNames: String,

    val sex: Int,

    val birthYear: Int,

    val phoneNumber: String?,

    val isActive: Boolean,

    @ColumnInfo(index = true)
    val regionId: Int
) : BaseDBO() {
    override fun getValue(field: Field): Any {
        return when (field.name) {
            "id" -> this.id
            "firstName" -> this.firstName
            "secondName" -> this.secondName
            "otherNames" -> this.otherNames
            "sex" -> this.sex
            "birthYear" -> this.birthYear
            "phoneNumber" -> this.phoneNumber ?: ""
            "isActive" -> this.isActive
            else -> ""
        }
    }
}
