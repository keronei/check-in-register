package com.keronei.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.keronei.data.local.dao.CheckInDao
import com.keronei.data.local.dao.MemberDao
import com.keronei.data.local.dao.RegionsDao
import com.keronei.data.local.entities.CheckInDBO
import com.keronei.data.local.entities.MemberDBO
import com.keronei.data.local.entities.RegionDBO

@Database(
    entities = [CheckInDBO::class, MemberDBO::class, RegionDBO::class],
    version = 1,
    exportSchema = false
)
abstract class KODatabase : RoomDatabase() {
    abstract fun checkInDao(): CheckInDao

    abstract fun memberDao(): MemberDao

    abstract fun regionDao(): RegionsDao

    companion object {
        fun buildDatabase(context: Context) : KODatabase {
           return Room.databaseBuilder(
                context.applicationContext,
                KODatabase::class.java,
                "kodatabase.db"
            ).build()
        }
    }
}