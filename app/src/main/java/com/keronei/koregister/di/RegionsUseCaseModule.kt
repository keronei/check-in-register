package com.keronei.koregister.di

import com.keronei.data.repository.RegionsRepositoryImpl
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
    fun providesCreateRegionUseCase(regionsRepositoryImpl: RegionsRepositoryImpl): CreateRegionUseCase {
        return CreateRegionUseCase(regionsRepositoryImpl)
    }

    @Provides
    fun providesQueryAllRegionsUseCase(regionsRepositoryImpl: RegionsRepositoryImpl): QueryAllRegionsUseCase {
        return QueryAllRegionsUseCase(regionsRepositoryImpl)
    }

    @Provides
    fun providesUpdateRegionUseCase(regionsRepositoryImpl: RegionsRepositoryImpl): UpdateRegionUseCase {
        return UpdateRegionUseCase(regionsRepositoryImpl)
    }

    @Provides
    fun providesDeleteRegionUseCase(regionsRepositoryImpl: RegionsRepositoryImpl): DeleteRegionUseCase {
        return DeleteRegionUseCase(regionsRepositoryImpl)
    }
}