package com.keronei.utils.export

import android.content.Context
import com.keronei.android.common.Constants.EXPORT_FILE_NAME
import com.keronei.android.common.Constants.SHEET_NAME
import com.keronei.data.local.entities.BaseDBO
import com.keronei.data.local.entities.MemberDBO
import com.keronei.data.local.entities.RegionDBO
import com.keronei.utils.workbook
import org.apache.poi.hssf.usermodel.HSSFSheet
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import java.io.File
import java.io.FileOutputStream

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

    private val sheet: HSSFSheet = hssfWorkBook.createSheet("$SHEET_NAME+$timeStamp")


    fun createExportFile(): HSSFWorkbook {
        populateRegionsData()
        populateMembersData()

        return hssfWorkBook
    }

    private fun populateRegionsData() {
        writeToSheet(0, regions, DBOType.REGION)
    }

    private fun populateMembersData() {
        writeToSheet(regions.size + 5, members, DBOType.MEMBER)
    }

    private fun writeToSheet(
        startingCell: Int,
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
        val header = sheet.createRow(startingCell)

        val guideHeader = header.createCell(startingCell)

        guideHeader.setCellValue("###$sectionName-$timeStamp-total-${items.size}###")

        val headerRow = sheet.createRow(startingCell + 1)

        for (field in fields) {

            val cellHeaderValue = headerRow.createCell(fields.indexOf(field) + 2)

            cellHeaderValue.setCellValue(field.name)

        }

        for (entry in items) {
            val dataRow = sheet.createRow(startingCell + 2)

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