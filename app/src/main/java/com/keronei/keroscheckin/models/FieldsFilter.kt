package com.keronei.keroscheckin.models

import com.keronei.keroscheckin.models.constants.ReportInclusion

object FieldsFilter {
    private val inclusions = mutableListOf<ReportInclusion>()

    fun addInclusion(entry: ReportInclusion) {
        inclusions.add(entry)
    }

    fun removeInclusion(entry: ReportInclusion) {
        inclusions.remove(entry)
    }

    fun clearAllInclusions() {
        inclusions.clear()
    }

    fun orderInclusions(): List<ReportInclusion> = inclusions
}
