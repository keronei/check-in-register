package com.keronei.domain.repository

import com.keronei.domain.entities.MemberEntity
import com.keronei.domain.entities.RegionEmbedEntity
import com.keronei.domain.entities.RegionEntity
import kotlinx.coroutines.flow.Flow

interface RegionsRepository {
    suspend fun createNewRegion(regionEntity: List<RegionEntity>)

    suspend fun getAllRegions(): Flow<List<RegionEntity>>

    suspend fun getAllRegionsWithMembersData(): Flow<List<RegionEmbedEntity>>

    suspend fun getMembersInARegion(regionId : Int): Flow<List<MemberEntity>>

    suspend fun updateRegion(regionEntity: RegionEntity)

    suspend fun deleteRegion(regionEntity: RegionEntity)

    suspend fun deleteAllRegions() : Int
}