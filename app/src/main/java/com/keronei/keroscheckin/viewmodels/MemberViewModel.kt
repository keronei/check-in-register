package com.keronei.keroscheckin.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keronei.domain.entities.MemberEntity
import com.keronei.domain.usecases.*
import com.keronei.domain.usecases.base.UseCaseParams
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MemberViewModel @Inject constructor(
    private val membersUseCases: MembersUseCases
) : ViewModel() {


    fun createNewMember(newMember: MemberEntity) {
        viewModelScope.launch {
            membersUseCases.createMemberUseCase(newMember)
        }
    }

    suspend fun queryAllMembers() = membersUseCases.queryAllMembersUseCase(UseCaseParams.Empty)

    fun updateMember(memberEntity: MemberEntity) {
        viewModelScope.launch {
            membersUseCases.updateMemberUseCase(memberEntity)
        }
    }

    fun deleteMember(memberEntity: MemberEntity) {
        viewModelScope.launch {
            membersUseCases.deleteMemberUseCase(memberEntity)
        }
    }

    fun deleteAllMembers() {
        viewModelScope.launch {
            membersUseCases.deleteAllMembersUseCase(Unit)
        }
    }
}