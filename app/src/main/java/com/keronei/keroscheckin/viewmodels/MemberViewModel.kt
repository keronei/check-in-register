package com.keronei.keroscheckin.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keronei.domain.entities.MemberEntity
import com.keronei.domain.usecases.CreateMemberUseCase
import com.keronei.domain.usecases.DeleteMemberUseCase
import com.keronei.domain.usecases.QueryAllMembersUseCase
import com.keronei.domain.usecases.UpdateMemberUseCase
import com.keronei.domain.usecases.base.UseCaseParams
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MemberViewModel @Inject constructor(
    private val addMemberUseCase: CreateMemberUseCase,
    private val queryAllMembersUseCase: QueryAllMembersUseCase,
    private val updateMemberUseCase: UpdateMemberUseCase,
    private val deleteMemberUseCase: DeleteMemberUseCase
) : ViewModel() {

    init {

    }

    fun createNewMember(newMember: MemberEntity) {
        viewModelScope.launch {
            addMemberUseCase(newMember)
        }
    }

    suspend fun queryAllMembers() = queryAllMembersUseCase(UseCaseParams.Empty)

    fun updateMember(memberEntity: MemberEntity) {
        viewModelScope.launch {
            updateMemberUseCase(memberEntity)
        }
    }

    fun deleteMember(memberEntity: MemberEntity){
        viewModelScope.launch {
            deleteMemberUseCase(memberEntity)
        }
    }

    fun deleteAllMembers(){

    }
}