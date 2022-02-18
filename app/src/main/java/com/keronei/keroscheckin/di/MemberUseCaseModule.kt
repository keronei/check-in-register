package com.keronei.keroscheckin.di

import com.keronei.domain.repository.MembersRepository
import com.keronei.domain.usecases.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object MemberUseCaseModule {
    @Provides
    fun providesMembersUseCase(membersRepository: MembersRepository): MembersUseCases {
        return MembersUseCases(
            CreateMemberUseCase(membersRepository),
            DeleteMemberUseCase(membersRepository),
            QueryAllMembersUseCase(membersRepository),
            UpdateMemberUseCase(membersRepository),
            DeleteAllMembersUseCase(membersRepository)
        )
    }

}