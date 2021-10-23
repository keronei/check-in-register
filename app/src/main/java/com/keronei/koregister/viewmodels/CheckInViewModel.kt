package com.keronei.koregister.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keronei.domain.entities.CheckInEntity
import com.keronei.domain.entities.MemberEntity
import com.keronei.domain.usecases.CheckInMemberUseCase
import com.keronei.domain.usecases.ListAttendeesUseCase
import com.keronei.domain.usecases.UndoCheckInUseCase
import com.keronei.domain.usecases.base.UseCaseParams
import com.keronei.domain.usecases.params.CheckInParam
import com.keronei.koregister.models.AttendeePresentation
import com.keronei.koregister.models.states.AttendeeViewState
import com.keronei.koregister.models.toPresentation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CheckInViewModel @Inject constructor(
    private val checkInMemberUseCase: CheckInMemberUseCase,
    private val undoCheckInUseCase: UndoCheckInUseCase,
    private val attendeesUseCase: ListAttendeesUseCase
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
            checkInMemberUseCase(CheckInParam(checkInEntity, memberEntity))
        }
    }

    fun undoCheckInForMember(checkInEntity: CheckInEntity) {
        viewModelScope.launch {
            undoCheckInUseCase(checkInEntity)
        }
    }

    suspend fun attendanceData() {
        attendeesUseCase(UseCaseParams.Empty).collect { newValues ->
            val list = mutableListOf<AttendeePresentation>()

            newValues.forEach { entity ->
                list.add(entity.toPresentation())
            }

            attendanceData.emit(AttendeeViewState(list))

        }

    }

}