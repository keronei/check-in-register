package com.keronei.koregister.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keronei.domain.entities.AttendanceEntity
import com.keronei.domain.usecases.ListAttendeesUseCase
import com.keronei.domain.usecases.QueryAllMembersUseCase
import com.keronei.domain.usecases.base.UseCaseParams
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllMembersViewModel @Inject constructor(private val queryAllMembersUseCase: ListAttendeesUseCase) :
    ViewModel() {

    val allMembersData = MutableStateFlow(value = listOf<AttendanceEntity>())

    init {
        viewModelScope.launch {
            queryAllMembersUseCase(UseCaseParams.Empty).collect { entries ->
                allMembersData.emit(entries)
            }
        }
    }

}