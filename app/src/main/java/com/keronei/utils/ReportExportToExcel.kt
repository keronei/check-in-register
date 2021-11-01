package com.keronei.utils

import android.content.Context
import android.content.Intent
import android.content.Intent.*
import android.os.Environment
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import com.keronei.domain.entities.AttendanceEntity
import com.keronei.keroscheckin.models.FieldsFilter
import com.keronei.keroscheckin.models.constants.ReportInclusion
import com.keronei.keroscheckin.models.constants.fieldsDictionary
import org.apache.poi.hssf.usermodel.HSSFCellStyle
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.hssf.util.HSSFColor
import org.apache.poi.ss.usermodel.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception
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


private const val EXCEL_SHEET_NAME = "Sheet1"

private val parser = SimpleDateFormat("hh:mm a", Locale.US)

fun exportDataIntoWorkbook(
    context: Context, fileName: String,
    dataList: List<AttendanceEntity>,
    fields: FieldsFilter
): Intent {
    workbook = HSSFWorkbook()

    cellStyle = workbook.createCellStyle();
    cellStyle.fillForegroundColor = HSSFColor.AQUA.index;
    cellStyle.fillPattern = HSSFCellStyle.SOLID_FOREGROUND;
    cellStyle.alignment = CellStyle.ALIGN_CENTER;

    // Creating a New HSSF Workbook (.xls format)

    sheet = workbook.createSheet(EXCEL_SHEET_NAME);

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

    val file = File(context.getExternalFilesDir(null), fileName)

    val fileOutputStream: FileOutputStream?

    fileOutputStream = FileOutputStream(file)
    workbook.write(fileOutputStream)


    val openGeneratedReportFileIntent = Intent()

    val uri = FileProvider.getUriForFile(context, context.packageName + ".fileprovider", file)

    openGeneratedReportFileIntent.setDataAndType(
        uri,
        "application/vnd.ms-excel"
    )

    openGeneratedReportFileIntent.putExtra(EXTRA_STREAM, uri)
    openGeneratedReportFileIntent.addFlags(FLAG_GRANT_READ_URI_PERMISSION)

    return openGeneratedReportFileIntent
}

/**
 * Checks if Storage is READ-ONLY
 *
 * @return boolean
 */
private fun isExternalStorageReadOnly(): Boolean {
    val externalStorageState: String = Environment.getExternalStorageState()
    return Environment.MEDIA_MOUNTED_READ_ONLY.equals(externalStorageState)
}

/**
 * Checks if Storage is Available
 *
 * @return boolean
 */
private fun isExternalStorageAvailable(): Boolean {
    val externalStorageState: String = Environment.getExternalStorageState()
    return Environment.MEDIA_MOUNTED.equals(externalStorageState)
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

    val headerRow: Row = sheet.createRow(0)
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
                ReportInclusion.AGE -> cell.setCellValue(dataList[i].memberEntity.age.toString())
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

/**
 * Store Excel Workbook in external storage
 *
 * @param context  - application context
 * @param fileName - name of workbook which will be stored in device
 * @return boolean - returns state whether workbook is written into storage or not
 */
private fun storeExcelInStorage(context: Context, fileName: String): Boolean {
    var isSuccess: Boolean
    val file = File(context.getExternalFilesDir(null), fileName)
    var fileOutputStream: FileOutputStream? = null
    try {
        fileOutputStream = FileOutputStream(file)
        workbook.write(fileOutputStream)
        Log.e(TAG, "Writing file$file")
        isSuccess = true
    } catch (e: IOException) {
        Log.e(TAG, "Error writing Exception: ", e)
        isSuccess = false
    } catch (e: Exception) {
        Log.e(TAG, "Failed to save file due to Exception: ", e)
        isSuccess = false
    } finally {
        try {
            fileOutputStream?.close()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
    return isSuccess
}