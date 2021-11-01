package com.keronei.keroscheckin.models

import com.keronei.keroscheckin.models.constants.ReportInclusion

data class FieldsFilter(
    val includePhone: Boolean = true,
    val includeRegion: Boolean = false,
    val includeAge: Boolean = false,
    val includeTemperature: Boolean = false,
    val includeCheckInTime: Boolean = false
) {
    private val inclusions = mutableListOf<ReportInclusion>()

    fun orderInclusions(): List<ReportInclusion> {
        inclusions.clear()

        if (includePhone) {
            inclusions.add(ReportInclusion.PHONE)
        }

        if (includeRegion) {
            inclusions.add(ReportInclusion.REGION)
        }

        if (includeAge) {
            inclusions.add(ReportInclusion.AGE)
        }

        if (includeTemperature) {
            inclusions.add(ReportInclusion.TEMPERATURE)
        }

        if (includeCheckInTime) {
            inclusions.add(ReportInclusion.CHECK_IN_TIME)
        }

        return inclusions
    }
}
