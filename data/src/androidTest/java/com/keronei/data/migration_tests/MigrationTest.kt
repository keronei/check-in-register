package com.keronei.data.migration_tests

import androidx.room.testing.MigrationTestHelper
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
                        "sex, age, phoneNumber, regionId) " +
                        "VALUES ('testuser','testuser','test',1,1991,'010121451',1)"
            )
            close()
        }

        database = helper.runMigrationsAndValidate(
            testDatabase,
            2,
            true,
            MIGRATION_1_2
        )


    }
}