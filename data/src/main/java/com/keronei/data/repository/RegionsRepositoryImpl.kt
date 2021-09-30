package com.keronei.data.repository

import com.keronei.data.local.dao.RegionsDao
import com.keronei.domain.entities.MemberEntity
import com.keronei.domain.entities.RegionEntity
import com.keronei.domain.repository.RegionsRepository
import kotlinx.coroutines.flow.Flow

class RegionsRepositoryImpl(private val regionsDao: RegionsDao) : RegionsRepository {
    override suspend fun createNewRegion(regionEntity: RegionEntity): Int {
        //regionsDao.createRegion()
        return 1
    }

    override suspend fun getAllRegions(): Flow<List<RegionEntity>> {
        TODO("Not yet implemented")
    }

    override suspend fun getMembersInARegion(): Flow<List<MemberEntity>> {
        TODO("Not yet implemented")
    }

    override suspend fun updateRegion(regionEntity: RegionEntity): Int {
        TODO("Not yet implemented")
    }

    override suspend fun deleteRegion(regionEntity: RegionEntity): Int {
        TODO("Not yet implemented")
    }
}