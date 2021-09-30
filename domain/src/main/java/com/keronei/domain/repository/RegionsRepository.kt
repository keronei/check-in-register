package com.keronei.domain.repository

import com.keronei.domain.entities.MemberEntity
import com.keronei.domain.entities.RegionEntity
import kotlinx.coroutines.flow.Flow

interface RegionsRepository {
    suspend fun createNewRegion(regionEntity: RegionEntity) : Int

    suspend fun getAllRegions(): Flow<List<RegionEntity>>

    suspend fun getMembersInARegion(): Flow<List<MemberEntity>>

    suspend fun updateRegion(regionEntity: RegionEntity) : Int

    suspend fun deleteRegion(regionEntity: RegionEntity) : Int
}