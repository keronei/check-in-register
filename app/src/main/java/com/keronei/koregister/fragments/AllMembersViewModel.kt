package com.keronei.koregister.fragments

import androidx.lifecycle.ViewModel
import com.keronei.domain.usecases.ListAttendeesUseCase
import com.keronei.domain.usecases.QueryAllMembersUseCase
import com.keronei.domain.usecases.base.UseCaseParams
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AllMembersViewModel @Inject constructor(private val queryAllMembersUseCase: ListAttendeesUseCase) : ViewModel() {

    suspend fun queryAllMembersAttendance() = queryAllMembersUseCase(UseCaseParams.Empty)

}