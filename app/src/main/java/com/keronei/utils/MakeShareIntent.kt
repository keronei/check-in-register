package com.keronei.utils

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import java.io.File
import java.io.FileOutputStream

fun makeShareIntent(fileName: String, workBookToWrite: HSSFWorkbook, context: Context): Intent {
    val file = File(context.getExternalFilesDir(null), "$fileName.xlsx")

    val fileOutputStream: FileOutputStream?

    fileOutputStream = FileOutputStream(file)

    workBookToWrite.write(fileOutputStream)

    val openGeneratedReportFileIntent = Intent()

    val uri = FileProvider.getUriForFile(context, context.packageName + ".fileprovider", file)

    openGeneratedReportFileIntent.setDataAndType(
        uri,
        "application/vnd.ms-excel"
    )

    openGeneratedReportFileIntent.putExtra(Intent.EXTRA_STREAM, uri)
    openGeneratedReportFileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    return openGeneratedReportFileIntent
}
