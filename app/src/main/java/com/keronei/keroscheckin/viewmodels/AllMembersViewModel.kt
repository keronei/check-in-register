package com.keronei.keroscheckin.viewmodels

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keronei.domain.entities.AttendanceEntity
import com.keronei.domain.usecases.AttendanceUseCases
import com.keronei.domain.usecases.ListAttendeesUseCase
import com.keronei.domain.usecases.MembersUseCases
import com.keronei.domain.usecases.base.UseCaseParams
import com.keronei.keroscheckin.R
import com.keronei.keroscheckin.instance.KORegisterApplication
import com.keronei.keroscheckin.models.AttendeePresentation
import com.keronei.keroscheckin.models.constants.CHECK_IN_INVALIDATE_DEFAULT_PERIOD
import com.keronei.keroscheckin.models.toPresentation
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllMembersViewModel @Inject constructor(
    private val attendanceUseCases: AttendanceUseCases,
    preferences: SharedPreferences,
    @ApplicationContext context: Context
) :
    ViewModel() {

    val allMembersData = MutableStateFlow(value = listOf<AttendeePresentation>())

    val allMembersDataInEntities = MutableStateFlow(value = listOf<AttendanceEntity>())

    val membersFabVisibilityStatus = MutableStateFlow(value = true)

    private var invalidationPeriod = CHECK_IN_INVALIDATE_DEFAULT_PERIOD.toInt()

    val membersFabVisibilityStatusPropagate: StateFlow<Boolean> = membersFabVisibilityStatus

    private val key = context.resources.getString(R.string.invalidate_period_key)

    init {
        viewModelScope.launch {

            invalidationPeriod =
                preferences.getString(key, CHECK_IN_INVALIDATE_DEFAULT_PERIOD)?.toInt()
                    ?: CHECK_IN_INVALIDATE_DEFAULT_PERIOD.toInt()

            attendanceUseCases.listAttendeesUseCase(UseCaseParams.Empty).collect { entries ->

                allMembersDataInEntities.emit(entries)

                val defaultList =
                    entries.map { entry -> entry.toPresentation(invalidationPeriod) }

                allMembersData.emit(defaultList)
            }
        }
    }

}