package com.keronei.koregister.models

data class FieldsFilter(
    val includePhone: Boolean = true,
    val includeRegion: Boolean = false,
    val includeAge: Boolean = false,
    val includeTemperature: Boolean = false,
    val includeCheckInTime: Boolean = false
)
