package com.keronei.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.keronei.data.local.dao.CheckInDao
import com.keronei.data.local.dao.MemberDao
import com.keronei.data.local.dao.RegionsDao
import com.keronei.data.local.entities.CheckInDBO
import com.keronei.data.local.entities.MemberDBO
import com.keronei.data.local.entities.RegionDBO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(
    entities = [CheckInDBO::class, MemberDBO::class, RegionDBO::class],
    version = 2,
    exportSchema = false
)
abstract class KODatabase : RoomDatabase() {
    abstract fun checkInDao(): CheckInDao

    abstract fun memberDao(): MemberDao

    abstract fun regionDao(): RegionsDao

    companion object {

        @Volatile
        private var databaseInstance: KODatabase? = null

        fun buildDatabase(context: Context, scope: CoroutineScope): KODatabase {
            return databaseInstance ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    KODatabase::class.java,
                    "kodatabase.db"
                ).addCallback(KODatabaseCallBack(scope)).fallbackToDestructiveMigration().build()
//TODO configure migration strategy
                databaseInstance = instance

                instance
            }
        }
    }


    private class KODatabaseCallBack(private val scope: CoroutineScope) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            databaseInstance?.let { readyInstance ->
                scope.launch {
                    val instance = readyInstance.regionDao()
                    instance.createRegion(RegionDBO(0, "Guest/Visitor"))
                }

            }

        }
    }
}