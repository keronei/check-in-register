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
    fun providesCreateRegionUseCase(regionsRepositoryImpl: RegionsRepository): CreateRegionUseCase {
        return CreateRegionUseCase(regionsRepositoryImpl)
    }

    @Provides
    fun providesQueryAllRegionsUseCase(regionsRepositoryImpl: RegionsRepository): QueryAllRegionsUseCase {
        return QueryAllRegionsUseCase(regionsRepositoryImpl)
    }

    @Provides
    fun providesUpdateRegionUseCase(regionsRepositoryImpl: RegionsRepository): UpdateRegionUseCase {
        return UpdateRegionUseCase(regionsRepositoryImpl)
    }

    @Provides
    fun providesDeleteRegionUseCase(regionsRepositoryImpl: RegionsRepository): DeleteRegionUseCase {
        return DeleteRegionUseCase(regionsRepositoryImpl)
    }

    @Provides
    fun providesQueryAllRegionsWithMemberDataUseCase(regionsRepositoryImpl: RegionsRepository): QueryAllRegionsWithMembersUseCase {
        return QueryAllRegionsWithMembersUseCase(regionsRepositoryImpl)
    }
}