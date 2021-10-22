package com.keronei.koregister.models

import android.os.Parcelable
import com.keronei.domain.entities.MemberEntity
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AttendeePresentation(
    val firstName: String,
    val secondName : String,
    val otherNames : String,
    val memberId : Int,
    val name: String,
    val age : Int,
    val phoneNumber : String?,
    val isActive : Boolean,
    val regionId : Int,
    val regionName : String,
    val lastCheckIn: String?
) : Parcelable