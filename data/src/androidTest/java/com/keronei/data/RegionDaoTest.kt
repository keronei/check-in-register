package com.keronei.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.keronei.data.local.KODatabase
import com.keronei.data.local.dao.CheckInDao
import com.keronei.data.local.dao.MemberDao
import com.keronei.data.local.dao.RegionsDao
import com.keronei.data.local.entities.RegionDBO
import com.keronei.data.repository.mapper.MemberLocalEntityMapper
import com.keronei.domain.entities.MemberEntity
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RegionDaoTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var koDatabase: KODatabase
    private lateinit var checkInDao: CheckInDao
    private lateinit var memberDao: MemberDao
    private lateinit var regionDao: RegionsDao
    private lateinit var member: MemberEntity
    private lateinit var region : RegionDBO
    private lateinit var memberLocalEntityMapper: MemberLocalEntityMapper

    @Before
    fun initialiseTest() {
        koDatabase = KOTestDatabaseInstance.initDatabase()
        checkInDao = koDatabase.checkInDao()
        memberDao = koDatabase.memberDao()
        regionDao = koDatabase.regionDao()
        region = KOTestDatabaseInstance.makeRegion()
        member = KOTestDatabaseInstance.getMemberEntity()
        memberLocalEntityMapper = MemberLocalEntityMapper()
    }

    @Test
    fun query_region_returns_empty_list_of_regions() {
        runBlocking {
            val emptyRegions = regionDao.queryAllRegions().first()
            assertTrue("No region has been created.", emptyRegions.isEmpty())
        }
    }

    @Test
    fun add_new_region_creates_new_region() {
        runBlocking {

            regionDao.createRegion(region)

            val singleRegionList = regionDao.queryAllRegions().first()

            assertEquals("Region name should match.", region.name, singleRegionList.first().name)

            assertTrue("Airport region created.", singleRegionList.size == 1)
        }
    }

    @Test
    fun update_region_updates_with_the_provided_information() {
        runBlocking {
            val region2 = RegionDBO(0, "Ring road")

            val updatedRegionName = RegionDBO(1, "West-airport")

            regionDao.createRegion(region)
            regionDao.createRegion(region2)

            regionDao.updateRegion(updatedRegionName)

            val regions = regionDao.queryAllRegions().first()

            assertEquals(regions.size, 2)
            assertEquals(
                "Region with id 1 should have updated name.",
                regions.first { regionDBO -> regionDBO.id == 1 }.name,
                updatedRegionName.name
            )

        }
    }

    @Test
    fun delete_region_deletes_region_with_specific_id() {
        runBlocking {
            val region2 = RegionDBO(0, "Ring road")

            regionDao.createRegion(region)
            regionDao.createRegion(region2)

            regionDao.deleteRegion(RegionDBO(2, region2.name))

            val remainingList = regionDao.queryAllRegions().first()

            assertEquals(
                "Region with name ${region.name} should have remained.",
                remainingList.first().name, region.name
            )
        }
    }

    @Test
    fun adding_several_regions_returns_all() {
        runBlocking {
            //1
            regionDao.createRegion(region)
            //2
            regionDao.createRegion(region)
            //3
            regionDao.createRegion(region)
            //4
            regionDao.createRegion(region)
            //5
            regionDao.createRegion(region)
            //6
            regionDao.createRegion(region)

            memberDao.createNewMember(memberLocalEntityMapper.map(KOTestDatabaseInstance.getMemberEntity()))

            regionDao.deleteRegion(RegionDBO(6, region.name))

            val remainingRegions = regionDao.queryAllRegions().first()

            assertTrue(remainingRegions.size == 5)
        }
    }

    @After
    fun cleanUpTestingData() {
        koDatabase.close()
    }
}