package com.keronei.data.local.dao

import androidx.room.*
import com.keronei.data.local.embeds.Attendance
import com.keronei.data.local.entities.MemberDBO
import kotlinx.coroutines.flow.Flow


@Dao
interface MemberDao {
    @Insert
    suspend fun createNewMember(memberDBO: MemberDBO)

    @Update(entity = MemberDBO::class)
    suspend fun updateMemberInformation(memberDBO: MemberDBO)

    @Query("SELECT * from MemberDBO ORDER BY firstName DESC")
    fun getAllMembers(): Flow<List<MemberDBO>>

    @Transaction
    @Query("SELECT * FROM MemberDBO")
    fun getAttendanceInformation(): Flow<List<Attendance>>

    @Delete
    suspend fun deleteMember(memberDBO: MemberDBO)

    @Query("DELETE FROM MemberDBO")
    suspend fun deleteAllMembers() : Int
}