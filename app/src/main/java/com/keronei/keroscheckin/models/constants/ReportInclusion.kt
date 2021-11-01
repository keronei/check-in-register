package com.keronei.keroscheckin.models.constants

enum class ReportInclusion {
    PHONE, REGION, AGE, TEMPERATURE, CHECK_IN_TIME
}

fun fieldsDictionary(): Map<ReportInclusion, String> {
    return mapOf(
        ReportInclusion.PHONE to "Phone",
        ReportInclusion.REGION to "Region",
        ReportInclusion.TEMPERATURE to "Temperature (ºC)",
        ReportInclusion.CHECK_IN_TIME to "Arrival time",
        ReportInclusion.AGE to "Age"
    )
}