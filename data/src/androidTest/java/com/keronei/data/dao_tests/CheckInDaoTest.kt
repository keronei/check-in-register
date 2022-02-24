package com.keronei.data.dao_tests

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.keronei.data.local.KODatabase
import com.keronei.data.local.dao.CheckInDao
import com.keronei.data.local.dao.MemberDao
import com.keronei.data.local.dao.RegionsDao
import com.keronei.data.local.entities.CheckInDBO
import com.keronei.data.local.entities.RegionDBO
import com.keronei.data.repository.mapper.MemberLocalEntityMapper
import com.keronei.domain.entities.MemberEntity
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
class CheckInDaoTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var koDatabase: KODatabase
    private lateinit var checkInDao: CheckInDao
    private lateinit var memberDao: MemberDao
    private lateinit var regionDao : RegionsDao
    private lateinit var member: MemberEntity
    private lateinit var memberLocalEntityMapper: MemberLocalEntityMapper

    @Before
    fun initialiseTest() {
        koDatabase = KOTestDatabaseInstance.initDatabase()
        checkInDao = koDatabase.checkInDao()
        memberDao = koDatabase.memberDao()
        regionDao = koDatabase.regionDao()
        member = KOTestDatabaseInstance.getMemberEntity()
        memberLocalEntityMapper = MemberLocalEntityMapper()
    }

    @Test
    fun checkIn_member_will_only_work_when_member_exists() {

        return runBlocking {
            createMember()

            val checkIn = createCheckIn()

            checkInDao.checkInMember(checkIn)

            val availableCheckIns = checkInDao.getAllCheckIns().first()

            assertTrue("One checkIn should exist.", availableCheckIns.isNotEmpty())
        }
    }


    @Test
    fun checkedIn_returns_true_if_member_has_done_so_within_8_hours() {
        runBlocking {
            createMember()
            val checkIn = createCheckIn()

            checkInDao.checkInMember(checkIn)

            val attendance = memberDao.getAttendanceInformation().first().first()

            val checkInInfo = attendance.checkIns

            assertTrue(
                "User has already checked In at least once.",
                checkInInfo.isNotEmpty()
            )

            val checkInData = checkInDao.getCheckInForMember(1, 0L)

            assertTrue(
                "Returned attendance data is $checkInInfo while checkIn info is $checkInData",
                checkInInfo == checkInData
            )

        }

    }

    @Test
    fun cancel_checkIn_eliminates_checkIn_info_for_provided_id() {
        runBlocking {
            createMember()

            val checkIn = createCheckIn()

            checkInDao.checkInMember(checkIn)

            checkInDao.cancelChecking(
                CheckInDBO(
                    1,
                    checkIn.memberId,
                    checkIn.timeStamp,
                    checkIn.temperature
                ).timeStamp
            )

            val checkIns = checkInDao.getCheckInForMember(1, 0L)

            assertTrue("CheckIn was cancelled but list is $checkIns.", checkIns.isEmpty())

        }
    }

    private fun createCheckIn(): CheckInDBO =
        CheckInDBO(0, 1, Calendar.getInstance().timeInMillis, 35.5)

    private suspend fun createMember() {
        regionDao.createRegion(listOf(RegionDBO(1, "Test Region.")))

        memberDao.createNewMembers(listOf(memberLocalEntityMapper.map(member)))
    }

    @After
    fun tearDownTest() {
        koDatabase.close()
    }
}