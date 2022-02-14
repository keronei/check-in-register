package com.keronei.keroscheckin.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AttendeePresentation(
    val firstName: String,
    val secondName : String,
    val otherNames : String,
    val sex : Int,
    val memberId : Int,
    val name: String,
    val age : Int,
    val isMarried : Boolean,
    val phoneNumber : String?,
    val isActive : Boolean,
    val regionId : Int,
    val regionName : String,
    val lastCheckIn: String?,
    val lastCheckInStamp : Long?
) : Parcelable