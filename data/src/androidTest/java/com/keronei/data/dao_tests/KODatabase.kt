package com.keronei.data.dao_tests

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.keronei.data.local.KODatabase
import com.keronei.data.local.entities.RegionDBO
import com.keronei.domain.entities.MemberEntity


object KOTestDatabaseInstance {

    private lateinit var koDatabase: KODatabase

    fun initDatabase(): KODatabase {
        koDatabase = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().context,
            KODatabase::class.java
        ).build()

        return koDatabase
    }

    fun getMemberEntity() = MemberEntity(
        0,
        "Alex",
        "Lemayan",
        "",
        1,
        1,
        false,
        null,
        true,
        1
    )

    fun makeRegion(): RegionDBO = RegionDBO(0, "Airport")
}