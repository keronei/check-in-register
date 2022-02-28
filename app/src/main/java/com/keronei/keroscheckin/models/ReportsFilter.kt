package com.keronei.keroscheckin.models

import com.keronei.android.common.Constants.ALL_SEX_SELECTOR
import com.keronei.domain.entities.AttendanceEntity
import java.util.*

data class ReportsFilter(
    val selectedDate: Calendar = Calendar.getInstance(),//default today
    val attendance: Boolean = true,//default present/checked In
    val sex: Int = ALL_SEX_SELECTOR,//default all or 3 because 1 - male, 0 - female, 2 - other
    val includeInactive: Boolean = false//default all, active == true/1, inactive == false/0
) {

    private val default24HoursInMillis = 86400000
    private val startOsSelectedDay = selectedDate

    fun generateFinalReportWithFiltersApplied(fullList: List<AttendanceEntity>): List<AttendanceEntity> {
        startOsSelectedDay.set(Calendar.HOUR_OF_DAY, 0)
        startOsSelectedDay.set(Calendar.MINUTE, 0)
        startOsSelectedDay.set(Calendar.SECOND, 0)

        val subjectListInTermsOfAttendance: List<AttendanceEntity>

        val subjectListInTermsOfActivity: List<AttendanceEntity>

        val endOfDateForSelected = startOsSelectedDay.timeInMillis + default24HoursInMillis

        val listFromSelectedDateCheckedIn = fullList.filter { entry ->
            return@filter entry.checkIns.find { checkIn ->

                return@find checkIn.timeStamp > startOsSelectedDay.timeInMillis && checkIn.timeStamp < endOfDateForSelected

            } != null
        }

        val listFromSelectedDateAbsent = fullList - listFromSelectedDateCheckedIn.toSet()

        subjectListInTermsOfAttendance =
            if (attendance) listFromSelectedDateCheckedIn else listFromSelectedDateAbsent

        val subjectListInTermsOfSex: List<AttendanceEntity> = if (sex == ALL_SEX_SELECTOR)
            subjectListInTermsOfAttendance
        else subjectListInTermsOfAttendance.filter { entry -> entry.memberEntity.sex == sex }

        subjectListInTermsOfActivity =
            if (includeInactive) subjectListInTermsOfSex else subjectListInTermsOfSex.filter { entry ->
                entry.memberEntity.isActive
            }

        return subjectListInTermsOfActivity

    }
}
