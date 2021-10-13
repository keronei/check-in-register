package com.keronei.koregister.di

import com.keronei.data.repository.MembersRepositoryImpl
import com.keronei.domain.repository.MembersRepository
import com.keronei.domain.usecases.CreateMemberUseCase
import com.keronei.domain.usecases.DeleteMemberUseCase
import com.keronei.domain.usecases.QueryAllMembersUseCase
import com.keronei.domain.usecases.UpdateMemberUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object MemberUseCaseModule {
    @Provides
    fun providesCreateMemberUseCase(membersRepository: MembersRepository): CreateMemberUseCase {
        return CreateMemberUseCase(membersRepository)
    }

    @Provides
    fun providesQueryAllMembersUseCase(membersRepositoryImpl: MembersRepository): QueryAllMembersUseCase {
        return QueryAllMembersUseCase(membersRepositoryImpl)
    }

    @Provides
    fun providesUpdateMemberUseCase(membersRepositoryImpl: MembersRepository): UpdateMemberUseCase {
        return UpdateMemberUseCase(membersRepositoryImpl)
    }

    @Provides
    fun providesDeleteMemberUseCase(membersRepositoryImpl: MembersRepository): DeleteMemberUseCase {
        return DeleteMemberUseCase(membersRepositoryImpl)
    }
}