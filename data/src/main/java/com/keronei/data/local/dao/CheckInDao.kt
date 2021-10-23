package com.keronei.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.keronei.data.local.entities.CheckInDBO
import com.keronei.data.local.entities.MemberDBO
import kotlinx.coroutines.flow.Flow

@Dao
interface CheckInDao {
    @Insert
    suspend fun checkInMember(checkInDBO: CheckInDBO)

    @Query("SELECT * FROM CheckInDBO WHERE memberId = :memberId AND timeStamp > :arrivalAfter")
    suspend fun getCheckInForMember(memberId: Int, arrivalAfter: Long) : List<CheckInDBO>

    @Query("SELECT * FROM CheckInDBO ORDER BY timeStamp DESC")
    fun getAllCheckIns() : Flow<List<CheckInDBO>>

    @Query("DELETE FROM CheckInDBO WHERE timeStamp = :checkInTimeStamp")
    suspend fun cancelChecking(checkInTimeStamp: Long)
}