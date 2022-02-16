package com.keronei.data.migration_tests

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.keronei.data.local.KODatabase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class MigrationTest {
    private val testDatabase = "migration-test-db"
    private lateinit var database : SupportSQLiteDatabase

    @get : Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        KODatabase::class.java,
        ArrayList(),
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    @Throws(IOException::class)
    fun migrateFrom1to2() {
         database = helper.createDatabase(testDatabase, 1).apply {
            //No need to create region, will use guest region which is default.

            execSQL("INSERT INTO RegionDBO(name) VALUES ('test region')")

            execSQL(
                "INSERT into MemberDBO(firstName, secondName, otherNames, sex, age, phoneNumber, isActive, regionId ) " +
                        "VALUES ('testuser','testUserSecondName', 'test', 1 , 1991, '010121451' ,1 ,1)".trimIndent()
            )

            close()
        }


        database = helper.runMigrationsAndValidate(
            testDatabase,
            2,
            true
        )

        val resultCursor = database.query("SELECT firstName, secondName, birthYear FROM MemberDBO")

        assertTrue(resultCursor.moveToFirst())

        val birthYearIndex = resultCursor.getColumnIndex("birthYear")
        val firstNameIndex = resultCursor.getColumnIndex("firstName")
        val secondNameIndex =resultCursor.getColumnIndex("secondName")

        val birthYear = resultCursor.getInt(birthYearIndex)
        val secondName = resultCursor.getString(secondNameIndex)
        val firstName = resultCursor.getString(firstNameIndex)

        assertEquals(birthYear, 1991)
        assertEquals(secondName, "testUserSecondName")
        assertEquals(firstName, "testuser")


    }
}