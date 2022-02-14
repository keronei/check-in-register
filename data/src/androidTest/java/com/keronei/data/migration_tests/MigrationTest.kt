package com.keronei.data.migration_tests

import androidx.room.migration.Migration
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.keronei.data.local.KODatabase
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class MigrationTest {
    private val testDatabase = "migration-test-db"

    @get : Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        KODatabase::class.java.canonicalName,
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    @Throws(IOException::class)
    fun migrateFrom1to2() {
        var database = helper.createDatabase(testDatabase, 1).apply {
            //No need to create region, will use guest region which is default.
            execSQL(
                "INSERT into MemberDBO(firstName, secondName, otherNames, " +
                        "sex, age, phoneNumber, isActive, regionId) " +
                        "VALUES ('testuser','testuser', 'test', 1 , 1991, '010121451' ,1 ,1)"
            )
            close()
        }

        val MIGRATION_1_2 = object : Migration(1, 2){
            override fun migrate(database: SupportSQLiteDatabase) {

                //database.execSQL("ALTER TABLE MemberDBO RENAME COLUMN age TO birthYear")

                database.execSQL("ALTER TABLE MemberDBO ADD COLUMN isMarried BOOLEAN NOT NULL DEFAULT 0")
            }
        }


        database = helper.runMigrationsAndValidate(
            testDatabase,
            2,
            true,
            MIGRATION_1_2
        )


    }
}