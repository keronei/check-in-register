package com.keronei.domain.usecases

data class RegionsUseCases(
    val createRegionUseCase: CreateRegionUseCase,
    val queryAllRegionsUseCase: QueryAllRegionsUseCase,
    val updateRegionUseCase: UpdateRegionUseCase,
    val deleteRegionUseCase: DeleteRegionUseCase,
    val queryAllRegionsWithMembersUseCase: QueryAllRegionsWithMembersUseCase,
    val deleteAllRegionsUseCase: DeleteAllRegionsUseCase
)
