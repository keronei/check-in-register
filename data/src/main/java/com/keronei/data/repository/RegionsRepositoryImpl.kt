package com.keronei.data.repository

import com.keronei.data.local.dao.RegionsDao
import com.keronei.data.repository.mapper.MemberDBOToEntityMapper
import com.keronei.data.repository.mapper.RegionDBOToRegionEntityMapper
import com.keronei.data.repository.mapper.RegionEntityToRegionDBOMapper
import com.keronei.domain.entities.MemberEntity
import com.keronei.domain.entities.RegionEntity
import com.keronei.domain.repository.RegionsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RegionsRepositoryImpl(
    private val regionsDao: RegionsDao,
    private val regionDBOToRegionEntityMapper: RegionDBOToRegionEntityMapper,
    private val regionEntityToRegionDBOMapper: RegionEntityToRegionDBOMapper,
    private val memberDBOToEntityMapper: MemberDBOToEntityMapper
) : RegionsRepository {
    override suspend fun createNewRegion(regionEntity: RegionEntity) {
        regionsDao.createRegion(regionEntityToRegionDBOMapper.map(regionEntity))
    }

    override suspend fun getAllRegions(): Flow<List<RegionEntity>> {
        return regionsDao.queryAllRegions()
            .map { dbo -> regionDBOToRegionEntityMapper.mapList(dbo) }
    }

    override suspend fun getMembersInARegion(regionId: Int): Flow<List<MemberEntity>> {
        return regionsDao.queryMembersInARegion(regionId)
            .map { members -> memberDBOToEntityMapper.mapList(members) }
    }

    override suspend fun updateRegion(regionEntity: RegionEntity) {
        regionsDao.updateRegion(regionEntityToRegionDBOMapper.map(regionEntity))
    }

    override suspend fun deleteRegion(regionEntity: RegionEntity) {
        regionsDao.deleteRegion(regionEntityToRegionDBOMapper.map(regionEntity))
    }
}