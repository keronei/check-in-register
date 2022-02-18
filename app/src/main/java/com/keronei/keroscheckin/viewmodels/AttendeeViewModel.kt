package com.keronei.keroscheckin.viewmodels

import androidx.lifecycle.ViewModel
import com.keronei.keroscheckin.models.states.AttendeeViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class AttendeeViewModel @Inject constructor() : ViewModel(){
    private val currentAttendeeState  = MutableStateFlow(value = AttendeeViewState(emptyList()))


}