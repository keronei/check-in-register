package com.keronei.keroscheckin.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keronei.domain.entities.AttendanceEntity
import com.keronei.domain.usecases.AttendanceUseCases
import com.keronei.domain.usecases.ListAttendeesUseCase
import com.keronei.domain.usecases.MembersUseCases
import com.keronei.domain.usecases.base.UseCaseParams
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllMembersViewModel @Inject constructor(private val attendanceUseCases: AttendanceUseCases) :
    ViewModel() {

    val allMembersData = MutableStateFlow(value = listOf<AttendanceEntity>())

    val membersFabVisibilityStatus = MutableStateFlow(value = true)

    val membersFabVisibilityStatusPropagate: StateFlow<Boolean> = membersFabVisibilityStatus

    init {
        viewModelScope.launch {
            attendanceUseCases.listAttendeesUseCase(UseCaseParams.Empty).collect { entries ->
                allMembersData.emit(entries)
            }
        }
    }

}