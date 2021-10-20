package com.keronei.koregister.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RegionPresentation(val id: String, val name: String, val memberCount : String) : Parcelable