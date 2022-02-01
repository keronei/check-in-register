package com.keronei.utils.export

import com.keronei.android.common.Constants.MEMBERS_SHEET_NAME
import com.keronei.android.common.Constants.REGIONS_SHEET_NAME
import com.keronei.data.local.entities.BaseDBO
import com.keronei.data.local.entities.MemberDBO
import com.keronei.data.local.entities.RegionDBO
import org.apache.poi.hssf.usermodel.HSSFSheet
import org.apache.poi.hssf.usermodel.HSSFWorkbook

enum class DBOType {
    REGION,
    MEMBER
}

class ExportRegionMembersProcessor(
    val regions: List<RegionDBO>,
    val members: List<MemberDBO>,
    private val timeStamp: Long
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

    private val regionsSheet: HSSFSheet = hssfWorkBook.createSheet("$REGIONS_SHEET_NAME+$timeStamp")

    private val membersSheet: HSSFSheet = hssfWorkBook.createSheet("$MEMBERS_SHEET_NAME+$timeStamp")


    fun createExportFile(): HSSFWorkbook {
        populateRegionsData()
        populateMembersData()

        return hssfWorkBook
    }

    private fun populateRegionsData() {
        writeToSheet(regionsSheet, regions, DBOType.REGION)
    }

    private fun populateMembersData() {
        writeToSheet(membersSheet, members, DBOType.MEMBER)
    }

    private fun writeToSheet(
        sheet : HSSFSheet,
        items: List<BaseDBO>,
        type: DBOType
    ): HSSFSheet {
        val fields =
            when (type) {
                DBOType.REGION -> RegionDBO::class.java.declaredFields
                DBOType.MEMBER -> MemberDBO::class.java.declaredFields
            }

        val sectionName = when (type) {
            DBOType.REGION -> "regions"
            DBOType.MEMBER -> "members"

        }
        val header = sheet.createRow(0)

        val guideHeader = header.createCell(0)

        guideHeader.setCellValue("###$sectionName-$timeStamp-total-${items.size}###")

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