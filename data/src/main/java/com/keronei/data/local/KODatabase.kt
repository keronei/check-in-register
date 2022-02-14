package com.keronei.data.local

import android.content.Context
import androidx.room.*
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
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
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2, spec = KODatabase.AutoMigrationSpecFrom1to2::class)
    ]
)
abstract class KODatabase : RoomDatabase() {
    abstract fun checkInDao(): CheckInDao

    abstract fun memberDao(): MemberDao

    abstract fun regionDao(): RegionsDao

    @RenameColumn(fromColumnName = "age", toColumnName = "birthYear", tableName = "MemberDBO")
    class AutoMigrationSpecFrom1to2 : AutoMigrationSpec



    companion object {

        @Volatile
        private var databaseInstance: KODatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2){
            override fun migrate(database: SupportSQLiteDatabase) {

                //database.execSQL("ALTER TABLE MemberDBO RENAME COLUMN age TO birthYear")

                database.execSQL("ALTER TABLE MemberDBO ADD COLUMN isMarried BOOLEAN NOT NULL DEFAULT 0")
            }
        }


        fun buildDatabase(context: Context, scope: CoroutineScope): KODatabase {
            return databaseInstance ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    KODatabase::class.java,
                    "kodatabase.db"
                ).addCallback(KODatabaseCallBack(scope)).addMigrations(MIGRATION_1_2) .build()
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