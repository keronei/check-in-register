package com.keronei.keroscheckin.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CheckInPresentation(
    val id: Int,
    val memberId: Int,
    val timeStamp: Long,
    val temperature: Double
) : Parcelable