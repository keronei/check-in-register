package com.keronei.data.repository_tests

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.keronei.data.dao_tests.KOTestDatabaseInstance
import com.keronei.data.local.dao.RegionsDao
import com.keronei.data.repository.MembersRepositoryImpl
import com.keronei.data.repository.mapper.*
import com.keronei.domain.repository.MembersRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class RegionsRepositoryTest {

    @get: Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    lateinit var memberRepository: MembersRepository

    private lateinit var regionDao : RegionsDao

    private lateinit var koDatabase: com.keronei.data.local.KODatabase

    @Before
    fun prepareRepo() {
        koDatabase = KOTestDatabaseInstance.initDatabase()

        regionDao = koDatabase.regionDao()

        memberRepository = MembersRepositoryImpl(
            koDatabase.memberDao(),
            MemberLocalEntityMapper(),
            MemberDBOToEntityMapper(),
            AttendanceEmbedToAttendanceEntityMapper(
                MemberDBOToEntityMapper(),
                CheckInDBOToEntityMapper(), RegionDBOToRegionEntityMapper(),
                ),
            )
    }

    @Test
    fun createMemberReturnsCreatedMember(){
        runBlocking {
            regionDao.createRegion(KOTestDatabaseInstance.makeRegion())

            memberRepository.addNewMember(KOTestDatabaseInstance.getMemberEntity())

            val createdMember = memberRepository.getAllMembers().first().first()

           assert(createdMember.firstName == KOTestDatabaseInstance.getMemberEntity().firstName)
        }
    }

    @Test
    fun deleteAllMembersWipesAll() {
        runBlocking {

            regionDao.createRegion(KOTestDatabaseInstance.makeRegion())
            memberRepository.addNewMember(KOTestDatabaseInstance.getMemberEntity())

            memberRepository.deleteAllMembers()

            assertTrue(memberRepository.getAllMembers().first().isEmpty())
        }
    }


}