package com.keronei.utils

import com.keronei.domain.entities.AttendanceEntity
import com.keronei.keroscheckin.models.FieldsFilter
import com.keronei.keroscheckin.models.constants.ReportInclusion
import com.keronei.keroscheckin.models.constants.fieldsDictionary
import org.apache.poi.hssf.usermodel.HSSFCellStyle
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.hssf.util.HSSFColor
import org.apache.poi.ss.usermodel.*
import java.text.SimpleDateFormat
import java.util.*


const val TAG = "REPORTS GEN"

// Global Variables
lateinit var cell: Cell
lateinit var sheet: Sheet
lateinit var row: Row

// New Workbook
lateinit var workbook: Workbook

// Cell style for a cell
lateinit var cellStyle: CellStyle


private val parser = SimpleDateFormat("hh:mm a", Locale.US)

fun exportDataIntoWorkbook(
    fileName: String,
    dataList: List<AttendanceEntity>,
    fields: FieldsFilter
): HSSFWorkbook {
    workbook = HSSFWorkbook()

    cellStyle = workbook.createCellStyle();
    cellStyle.fillForegroundColor = HSSFColor.AQUA.index;
    cellStyle.fillPattern = HSSFCellStyle.SOLID_FOREGROUND;
    cellStyle.alignment = CellStyle.ALIGN_CENTER;

    // Creating a New HSSF Workbook (.xls format)

    sheet = workbook.createSheet(fileName);

    setHeaderCellStyle()

    row = sheet.createRow(0)
    // Creating a New Sheet and Setting width for each column

    sheet.setColumnWidth(0, 15 * 400) //name - default.

    val selections = fields.orderInclusions()

    for (field in selections) {
        sheet.setColumnWidth(selections.indexOf(field) + 1, 15 * 400)

    }

    setHeaderRow(fields)
    fillDataIntoExcel(dataList, fields)


    return workbook as HSSFWorkbook
}


/**
 * Setup header cell style
 */
private fun setHeaderCellStyle() {
    val headerCellStyle = workbook.createCellStyle()
    headerCellStyle.fillForegroundColor = HSSFColor.AQUA.index
    headerCellStyle.fillPattern = HSSFCellStyle.SOLID_FOREGROUND
    headerCellStyle.alignment = CellStyle.ALIGN_CENTER
}

/**
 * Setup Header Row
 */
private fun setHeaderRow(fields: FieldsFilter) {

    row = sheet.createRow(0)
    cell = row.createCell(0)
    cell.setCellValue("Name")
    cell.cellStyle = cellStyle

    val include = fields.orderInclusions()

    val fieldsNaming = fieldsDictionary()

    for (selectedField in include) {
        cell = row.createCell(include.indexOf(selectedField) + 1)
        cell.setCellValue(fieldsNaming[selectedField])
        cell.cellStyle = cellStyle
    }

}

/**
 * Fills Data into Excel Sheet
 *
 *
 * NOTE: Set row index as i+1 since 0th index belongs to header row
 *
 * @param dataList - List containing data to be filled into excel
 */
private fun fillDataIntoExcel(dataList: List<AttendanceEntity>, fields: FieldsFilter) {
    for (i in dataList.indices) {
        // Create a New Row for every new entry in list
        val rowData: Row = sheet.createRow(i + 1)

        // Create Cells for each row
        cell = rowData.createCell(0)
        cell.setCellValue(dataList[i].memberEntity.firstName + " " + dataList[i].memberEntity.secondName + " " + dataList[i].memberEntity.otherNames)

        val userSelection = fields.orderInclusions()

        for (selection in userSelection) {

            cell = rowData.createCell(userSelection.indexOf(selection) + 1)

            when (selection) {
                ReportInclusion.PHONE -> cell.setCellValue(dataList[i].memberEntity.phoneNumber)
                ReportInclusion.REGION -> cell.setCellValue(dataList[i].regionEntity.name)
                ReportInclusion.AGE -> {
                    //convert YOB to Age

                    val calendarInstance = Calendar.getInstance()
                    val currentYear = calendarInstance.get(Calendar.YEAR)

                    val actualAge = currentYear - dataList[i].memberEntity.age

                    val outputtedAgeValue: String = if (actualAge < 1) {
                        "~1"
                    } else {
                        actualAge.toString()
                    }

                    cell.setCellValue(outputtedAgeValue)
                }
                ReportInclusion.TEMPERATURE -> cell.setCellValue(if (dataList[i].checkIns.isEmpty()) "" else dataList[i].checkIns.first().temperature.toString())
                ReportInclusion.CHECK_IN_TIME -> cell.setCellValue(
                    if (dataList[i].checkIns.isEmpty()) "" else parser.format(
                        Date(dataList[i].checkIns.first().timeStamp)
                    )
                )
            }

        }

    }
}

