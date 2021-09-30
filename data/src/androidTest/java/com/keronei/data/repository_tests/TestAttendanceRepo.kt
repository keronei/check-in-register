package com.keronei.data.repository_tests

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.keronei.data.dao_tests.KOTestDatabaseInstance
import com.keronei.data.local.KODatabase
import com.keronei.data.local.dao.CheckInDao
import com.keronei.data.local.dao.MemberDao
import com.keronei.data.local.dao.RegionsDao
import com.keronei.data.repository.AttendanceDataRepositoryImpl
import com.keronei.data.repository.mapper.CheckInEntityLocalMapper
import com.keronei.data.repository.mapper.MemberDBOToEntityMapper
import com.keronei.data.repository.mapper.MemberLocalEntityMapper
import com.keronei.domain.entities.MemberEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TestAttendanceRepo {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var attendanceDataRepositoryImpl: AttendanceDataRepositoryImpl
    private lateinit var koDatabase: KODatabase
    private lateinit var checkInDao: CheckInDao
    private lateinit var memberDao: MemberDao
    private lateinit var regionDao: RegionsDao
    private lateinit var member: MemberEntity
    private lateinit var checkInEntityLocalMapper: CheckInEntityLocalMapper
    private lateinit var memberLocalEntityMapper: MemberLocalEntityMapper
    private lateinit var memberDBOToEntityMapper: MemberDBOToEntityMapper

    @Before
    fun initTests() {
        koDatabase = KOTestDatabaseInstance.initDatabase()
        checkInDao = koDatabase.checkInDao()
        memberDao = koDatabase.memberDao()
        regionDao = koDatabase.regionDao()
        member = KOTestDatabaseInstance.getMemberEntity()
        memberLocalEntityMapper = MemberLocalEntityMapper()
        checkInEntityLocalMapper = CheckInEntityLocalMapper()
        memberDBOToEntityMapper = MemberDBOToEntityMapper()

        attendanceDataRepositoryImpl =
            AttendanceDataRepositoryImpl(
                checkInDao,
                memberLocalEntityMapper,
                checkInEntityLocalMapper
            )
    }

    @Test
    fun create_region_creates_region(){
        runBlocking {
            attendanceDataRepositoryImpl.
        }
    }

    @After
    fun cleanUpTests() {

    }
}