package com.keronei.data.local.dao

import androidx.room.*
import com.keronei.data.local.entities.RegionDBO
import kotlinx.coroutines.flow.Flow

@Dao
interface RegionsDao {
    @Insert
    suspend fun createRegion(regionDBO: RegionDBO)

    @Query("SELECT * FROM RegionDBO ORDER BY name DESC")
    fun queryAllRegions() : Flow<List<RegionDBO>>

    @Update
    suspend fun updateRegion(regionDBO: RegionDBO)

    @Delete
    suspend fun deleteRegion(regionDBO: RegionDBO)
}