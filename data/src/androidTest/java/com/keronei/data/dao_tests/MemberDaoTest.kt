package com.keronei.data.dao_tests

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.keronei.data.local.KODatabase
import com.keronei.data.local.dao.MemberDao
import com.keronei.data.local.dao.RegionsDao
import com.keronei.data.local.entities.RegionDBO
import com.keronei.data.repository.mapper.MemberLocalEntityMapper
import com.keronei.domain.entities.MemberEntity
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class MemberDaoTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var koDatabase: KODatabase
    private lateinit var memberDao: MemberDao
    private lateinit var regionsDao : RegionsDao

    private lateinit var memberLocalEntityMapper: MemberLocalEntityMapper

    private lateinit var  member : MemberEntity
    private val member2 = MemberEntity(
        0,
        "Mathew",
        "Leteiya",
        "",
        1,
        14,
        true,
        null,
        true,
        1
    )

    @Before
    fun initialiseDb() {
        koDatabase = KOTestDatabaseInstance.initDatabase()
        memberDao = koDatabase.memberDao()
        regionsDao = koDatabase.regionDao()
        memberLocalEntityMapper = MemberLocalEntityMapper()
        member = KOTestDatabaseInstance.getMemberEntity()
    }

    @Test
    fun get_members_without_creation_returns_an_empty_list() {
        return runBlocking {

            val members = memberDao.getAllMembers().first()

            assertThat(members.isEmpty())
        }
    }

    @Test
    fun create_member_generates_new_id() {
        return runBlocking {
            createMember()

            val createdMembers = memberDao.getAllMembers().first()

            assertEquals(createdMembers.first().id, 1)
        }
    }


    @Test
    fun adding_a_member_returns_exactly_one_member() {

        return runBlocking {
            createMember()

            val createdMembers = memberDao.getAllMembers().first()

            assertEquals(createdMembers.size, 1)

            assertThat(createdMembers.first().firstName).isEqualTo(
                memberLocalEntityMapper.map(
                    member
                ).firstName
            )
        }
    }

    @Test
    fun creating_two_members_and_deleting_returns_a_single_member() {
        return runBlocking {

            createMember()

            memberDao.createNewMember(listOf(memberLocalEntityMapper.map(member2)))

            memberDao.deleteMember(memberLocalEntityMapper.map(member))

            val remainingMember = memberDao.getAllMembers().first()

            assertThat(remainingMember.size == 1)

        }
    }

    @Test
    fun adding_several_members_returns_correct_count() {
        return runBlocking {
            createMember()

            memberDao.createNewMember(listOf(memberLocalEntityMapper.map(member2)))

            createMember()

            createMember()

            val remainingMember = memberDao.getAllMembers().first()

            assertTrue("Created members total 4.", remainingMember.size == 4)
        }
    }

    @Test
    fun update_member_information_returns_correct_info() {
        return runBlocking {
            createMember()

            val updatedMember =
                MemberEntity(
                    1,
                    "Kimani",
                    "Mathenge",
                    "Mathias",
                    1,
                    15,
                    false,
                    "0100",
                    true,
                    1
                )

            memberDao.createNewMember(listOf(memberLocalEntityMapper.map(member2)))

            memberDao.updateMemberInformation(memberLocalEntityMapper.map(updatedMember))

            val queriedUpdatedMember =
                memberDao.getAllMembers().first().first { memberDBO -> memberDBO.id == 1 }

            assertTrue(
                "Updated second name is ${updatedMember.secondName} returned is ${queriedUpdatedMember.secondName}",
                updatedMember.secondName == queriedUpdatedMember.secondName
            )


        }
    }

    @Test
    fun deleteAllMembersDeletesCreatedMembers(){
        return runBlocking {
            createMember()
            memberDao.createNewMember(listOf(memberLocalEntityMapper.map(member2)))

            memberDao.deleteAllMembers()

            val existingMembersCount = memberDao.getAllMembers().first().size

            assertTrue(existingMembersCount == 0)
        }

    }

    private suspend fun createMember() {
        regionsDao.createRegion(listOf(RegionDBO(0, "Test region.")))
        memberDao.createNewMember(memberDBOs = listOf(memberLocalEntityMapper.map(member)))
    }

    @After
    fun closeDatabase() {
        koDatabase.close()
    }
}