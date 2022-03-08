package com.keronei.data.local.dao

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.keronei.data.local.embeds.RegionEmbed
import com.keronei.data.local.entities.MemberDBO
import com.keronei.data.local.entities.RegionDBO
import kotlinx.coroutines.flow.Flow

@Dao
interface RegionsDao {
    @Insert(onConflict = REPLACE)
    suspend fun createRegion(regionDBOs: RegionDBO) :Long

    @Query("SELECT * FROM RegionDBO ORDER BY name DESC")
    fun queryAllRegions(): Flow<List<RegionDBO>>

    @Transaction
    @Query("SELECT * FROM RegionDBO ORDER BY name DESC")
    fun queryAllRegionsWithMemberCount(): Flow<List<RegionEmbed>>

    @Query("SELECT * FROM MemberDBO WHERE regionId = :regionId ORDER BY firstName DESC")
    fun queryMembersInARegion(regionId: Int): Flow<List<MemberDBO>>

    @Update
    suspend fun updateRegion(regionDBO: RegionDBO)

    @Delete
    suspend fun deleteRegion(regionDBO: RegionDBO) : Int

    /**
     * This is not functional when the list is long, 1k+.
     */
    @Query("DELETE FROM RegionDBO WHERE id in (:regionsIds)")
    suspend fun _deleteAllRegions(regionsIds: List<Int>) : Int

}