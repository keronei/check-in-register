package com.keronei.utils

import android.content.Context
import android.content.Intent
import android.content.Intent.*
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import com.keronei.domain.entities.AttendanceEntity
import com.keronei.koregister.models.FieldsFilter
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
    val isWorkbookWrittenIntoStorage: Boolean

    // Check if available and not read only
//    if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
//        Log.e(TAG, "Storage not available or read only")
//        return false
//    }
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
    sheet.setColumnWidth(1, 15 * 400)
    sheet.setColumnWidth(2, 15 * 400)
    sheet.setColumnWidth(3, 15 * 400)
    sheet.setColumnWidth(4, 15 * 400)

    setHeaderRow(fields)
    fillDataIntoExcel(dataList)

    val file = File(context.getExternalFilesDir(null), fileName)

    val fileOutputStream: FileOutputStream?

    fileOutputStream = FileOutputStream(file)
    workbook.write(fileOutputStream)


    val openGeneratedReportFileIntent = Intent()

    val uri = FileProvider.getUriForFile(context, context.packageName + ".fileprovider", file)

    openGeneratedReportFileIntent.setDataAndType(
        uri,
        MimeTypeMap.getSingleton().getMimeTypeFromExtension(".xlsx")
    )

    openGeneratedReportFileIntent.putExtra(Intent.EXTRA_STREAM, uri)
    openGeneratedReportFileIntent.addFlags(FLAG_GRANT_READ_URI_PERMISSION)

    //isWorkbookWrittenIntoStorage = storeExcelInStorage(context, fileName)
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
    cell = row.createCell(1)
    cell.setCellValue("Phone")
    cell.cellStyle = cellStyle
    cell = row.createCell(2)
    cell.setCellValue("Region")
    cell.cellStyle = cellStyle
    cell = row.createCell(3)
    cell.setCellValue("Temperature ºC")
    cell.cellStyle = cellStyle
    cell = row.createCell(4)
    cell.setCellValue("Arrival time")
    cell.cellStyle = cellStyle

}

/**
 * Fills Data into Excel Sheet
 *
 *
 * NOTE: Set row index as i+1 since 0th index belongs to header row
 *
 * @param dataList - List containing data to be filled into excel
 */
private fun fillDataIntoExcel(dataList: List<AttendanceEntity>) {
    for (i in dataList.indices) {
        // Create a New Row for every new entry in list
        val rowData: Row = sheet.createRow(i + 1)

        // Create Cells for each row
        cell = rowData.createCell(0)
        cell.setCellValue(dataList[i].memberEntity.firstName + " " + dataList[i].memberEntity.secondName + " " + dataList[i].memberEntity.otherNames)
        cell = rowData.createCell(1)
        cell.setCellValue(dataList[i].memberEntity.phoneNumber)
        cell = rowData.createCell(2)
        cell.setCellValue(dataList[i].regionEntity.name)
        cell = rowData.createCell(3)
        cell.setCellValue(if (dataList[i].checkIns.isEmpty()) "" else dataList[i].checkIns.first().temperature.toString())
        cell = rowData.createCell(4)
        cell.setCellValue(if (dataList[i].checkIns.isEmpty()) "" else parser.format(Date(dataList[i].checkIns.first().timeStamp)))
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