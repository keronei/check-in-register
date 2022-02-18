package com.keronei.keroscheckin.di

import com.keronei.domain.repository.RegionsRepository
import com.keronei.domain.usecases.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RegionsUseCaseModule {
    @Provides
    fun providesCreateRegionUseCase(regionsRepositoryImpl: RegionsRepository): RegionsUseCases {
        return RegionsUseCases(
            CreateRegionUseCase(regionsRepositoryImpl),
            QueryAllRegionsUseCase
                (regionsRepositoryImpl),
            UpdateRegionUseCase
                (regionsRepositoryImpl),
            DeleteRegionUseCase
                (regionsRepositoryImpl),
            QueryAllRegionsWithMembersUseCase(regionsRepositoryImpl),
            DeleteAllRegionsUseCase(regionsRepositoryImpl)
        )
    }

}