package com.keronei.keroscheckin.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keronei.domain.entities.MemberEntity
import com.keronei.domain.usecases.MembersUseCases
import com.keronei.domain.usecases.base.UseCaseParams
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MemberViewModel @Inject constructor(
    private val membersUseCases: MembersUseCases
) : ViewModel() {


    suspend fun createNewMember(newMember: List<MemberEntity>) : List<Long> {
       val insertionCount = viewModelScope.async {
            membersUseCases.createMemberUseCase(newMember)
        }
        return insertionCount.await()
    }

    suspend fun queryAllMembers() = membersUseCases.queryAllMembersUseCase(UseCaseParams.Empty)

    suspend fun updateMember(memberEntity: MemberEntity) : Int {
      val updateCount = viewModelScope.async {
            membersUseCases.updateMemberUseCase(memberEntity)
        }

        return updateCount.await()
    }

    suspend fun deleteMember(memberEntity: MemberEntity) : Int {
       return withContext(viewModelScope.coroutineContext) {
           membersUseCases.deleteMemberUseCase(memberEntity)
       }
    }

    suspend fun deleteAllMembers() : Int {
       return withContext(viewModelScope.coroutineContext) {
           membersUseCases.deleteAllMembersUseCase(Unit)
       }
    }
}