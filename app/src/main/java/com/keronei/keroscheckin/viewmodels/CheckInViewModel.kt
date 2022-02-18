package com.keronei.keroscheckin.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keronei.domain.entities.CheckInEntity
import com.keronei.domain.entities.MemberEntity
import com.keronei.domain.usecases.AttendanceUseCases
import com.keronei.domain.usecases.CheckInMemberUseCase
import com.keronei.domain.usecases.ListAttendeesUseCase
import com.keronei.domain.usecases.UndoCheckInUseCase
import com.keronei.domain.usecases.base.UseCaseParams
import com.keronei.domain.usecases.params.CheckInParam
import com.keronei.keroscheckin.models.AttendeePresentation
import com.keronei.keroscheckin.models.states.AttendeeViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CheckInViewModel @Inject constructor(
    private val attendanceUseCases: AttendanceUseCases
) : ViewModel() {

    private val attendanceData = MutableStateFlow(value = AttendeeViewState(emptyList()))

    val customSelectedTime = MutableStateFlow(value = Calendar.getInstance())

    init {
        viewModelScope.launch {
            attendanceData.emit(AttendeeViewState(emptyList()))
        }
    }

    fun checkInMember(checkInEntity: CheckInEntity, memberEntity: MemberEntity) {
        viewModelScope.launch {
            attendanceUseCases.checkInMemberUseCase(CheckInParam(checkInEntity, memberEntity))
        }
    }

    fun undoCheckInForMember(checkInEntity: CheckInEntity) {
        viewModelScope.launch {
            attendanceUseCases.undoCheckInUseCase(checkInEntity)
        }
    }

    suspend fun attendanceData() {
        attendanceUseCases.listAttendeesUseCase (UseCaseParams.Empty).collect { _ ->
            val list = mutableListOf<AttendeePresentation>()
            attendanceData.emit(AttendeeViewState(list))

        }

    }

}