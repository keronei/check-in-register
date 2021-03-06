package com.keronei.domain.repository

import com.keronei.domain.entities.MemberEntity
import com.keronei.domain.entities.RegionEmbedEntity
import com.keronei.domain.entities.RegionEntity
import kotlinx.coroutines.flow.Flow

interface RegionsRepository {
    suspend fun createNewRegion(regionEntity: RegionEntity) : Long

    suspend fun getAllRegions(): Flow<List<RegionEntity>>

    suspend fun getAllRegionsWithMembersData(): Flow<List<RegionEmbedEntity>>

    suspend fun getMembersInARegion(regionId : Int): Flow<List<MemberEntity>>

    suspend fun updateRegion(regionEntity: RegionEntity)

    suspend fun deleteRegion(regionEntity: RegionEntity) : Int

    suspend fun deleteAllRegions(deletableRegions : List<RegionEntity>) : Int
}