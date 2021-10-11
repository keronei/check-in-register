package com.keronei.koregister.viewmodels

import androidx.lifecycle.ViewModel
import com.keronei.domain.usecases.SyncAttendanceUseCase
import com.keronei.koregister.models.states.AttendeeViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class AttendeeViewModel @Inject constructor(private val syncAttendanceUseCase: SyncAttendanceUseCase) : ViewModel(){
    private val currentAttendeeState  = MutableStateFlow(value = AttendeeViewState(emptyList()))


}