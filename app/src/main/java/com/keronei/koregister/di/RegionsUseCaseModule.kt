package com.keronei.koregister.di

import com.keronei.data.repository.RegionsRepositoryImpl
import com.keronei.domain.repository.RegionsRepository
import com.keronei.domain.usecases.CreateRegionUseCase
import com.keronei.domain.usecases.DeleteRegionUseCase
import com.keronei.domain.usecases.QueryAllRegionsUseCase
import com.keronei.domain.usecases.UpdateRegionUseCase
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
}