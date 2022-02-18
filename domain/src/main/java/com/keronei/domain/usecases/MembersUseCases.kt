package com.keronei.domain.usecases

data class MembersUseCases(
    val createMemberUseCase: CreateMemberUseCase,
    val deleteMemberUseCase: DeleteMemberUseCase,
    val queryAllMembersUseCase: QueryAllMembersUseCase,
    val updateMemberUseCase: UpdateMemberUseCase,
    val deleteAllMembersUseCase: DeleteAllMembersUseCase
)
