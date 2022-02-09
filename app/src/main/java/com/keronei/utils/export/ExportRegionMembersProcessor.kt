package com.keronei.utils.export

import android.provider.Settings.Global.getString
import com.keronei.android.common.Constants.MEMBERS_SHEET_NAME
import com.keronei.android.common.Constants.REGIONS_SHEET_NAME
import com.keronei.android.common.Constants.THREE_HASHES
import com.keronei.android.common.Constants.TOTAL_FIX
import com.keronei.android.common.Constants.VERSION_FIX
import com.keronei.domain.entities.BaseEntity
import com.keronei.domain.entities.MemberEntity
import com.keronei.domain.entities.RegionEntity
import com.keronei.keroscheckin.R
import org.apache.poi.hssf.usermodel.HSSFSheet
import org.apache.poi.hssf.usermodel.HSSFWorkbook

enum class EntityType {
    REGION,
    MEMBER
}

class ExportRegionMembersProcessor(
    val regions: List<RegionEntity>,
    val members: List<MemberEntity>,
    private val timeStamp: Long,
    private val versionNumber : String
) {
    /*
    Exporting data from room:
    @RegionDBO and @MemberBDO

    ###regions-10215541022102-total-10###
    id,name

    ###members-10215541022102-total-172###
    id,firstName,secondName,otherNames,sex,birthYear,phoneNumber,isActive,regionId

     */
    private val hssfWorkBook = HSSFWorkbook()

    private val regionsSheet: HSSFSheet = hssfWorkBook.createSheet("$REGIONS_SHEET_NAME$timeStamp")

    private val membersSheet: HSSFSheet = hssfWorkBook.createSheet("$MEMBERS_SHEET_NAME$timeStamp")


    fun createExportFile(): HSSFWorkbook {
        populateRegionsData()
        populateMembersData()

        return hssfWorkBook
    }

    private fun populateRegionsData() {
        writeToSheet(regionsSheet, regions, EntityType.REGION)
    }

    private fun populateMembersData() {
        writeToSheet(membersSheet, members, EntityType.MEMBER)
    }

    private fun writeToSheet(
        sheet : HSSFSheet,
        items: List<BaseEntity>,
        type: EntityType
    ): HSSFSheet {
        val fields =
            when (type) {
                EntityType.REGION -> RegionEntity::class.java.declaredFields
                EntityType.MEMBER -> MemberEntity::class.java.declaredFields
            }

        val sectionName = when (type) {
            EntityType.REGION -> "regions"
            EntityType.MEMBER -> "members"

        }
        val header = sheet.createRow(0)

        val guideHeader = header.createCell(0)

        guideHeader.setCellValue("$THREE_HASHES$sectionName-$timeStamp$VERSION_FIX$versionNumber$TOTAL_FIX${items.size}$THREE_HASHES")

        val headerRow = sheet.createRow(1)

        for (field in fields) {

            val cellHeaderValue = headerRow.createCell(fields.indexOf(field))

            cellHeaderValue.setCellValue(field.name)

        }

        for (entry in items) {
            val dataRow = sheet.createRow(items.indexOf(entry) + 2 )

            for (field in fields) {
                val cellValue = dataRow.createCell(fields.indexOf(field))

                val fieldType = field.genericType

                val fieldTypeAsClass = fieldType.javaClass

                val value = entry.getValue(field)

                cellValue.setCellValue((value.toString()))
            }
        }

        return sheet

    }


}