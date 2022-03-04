package com.keronei.utils.import


import android.util.Log
import com.keronei.android.common.Constants.THREE_HASHES
import com.keronei.android.common.Constants.TOTAL_FIX
import com.keronei.android.common.Constants.VERSION_FIX
import com.keronei.domain.entities.BaseEntity
import com.keronei.domain.entities.MemberEntity
import com.keronei.domain.entities.RegionEntity
import com.keronei.keroscheckin.models.ImportSummary
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import timber.log.Timber
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream

class ImportRegionMembersProcessor(loadedFile: InputStream) {

    private var fileInputStream: FileInputStream? = null

    private lateinit var importedWorkbook: Workbook

    private lateinit var regionsSheet: Sheet

    private lateinit var membersSheet: Sheet

    init {
        try {
            importedWorkbook = HSSFWorkbook(loadedFile)

            regionsSheet = importedWorkbook.getSheetAt(0)

            membersSheet = importedWorkbook.getSheetAt(1)

        } catch (ioException: IOException) {
            Timber.log(Log.ERROR, "IO exception loading workbook for import", ioException)
            ioException.printStackTrace()
        } catch (exception: Exception) {
            Timber.log(Log.ERROR, "General exception opening workbook", exception)
            exception.printStackTrace()
        } finally {
            try {
                fileInputStream?.close()
            } catch (exception: Exception) {
                Timber.log(Log.ERROR, "Closing inputStream for import", exception)
                exception.printStackTrace()
            }
        }
    }

    fun readEntries(): HashMap<List<RegionEntity>, List<MemberEntity>> {
        val regions = readRegions()

        val members = readMembers()

        return hashMapOf(regions to members)

    }

    fun readBasicInformation(): ImportSummary {
        if (!this::regionsSheet.isInitialized || !this::membersSheet.isInitialized) {
            return ImportSummary("", 0, 0)
        }

        val senderAppVersion = regionsSheet.first()
        val memberSheetFirstEntry = membersSheet.first().getCell(0).stringCellValue

        val readString = senderAppVersion.getCell(0).stringCellValue

        return ImportSummary(
            readAppVersion(readString),
            readEntriesCount(readString),
            readEntriesCount(memberSheetFirstEntry)
        )
    }

    private fun readAppVersion(readString: String): String {
        //"$THREE_HASHES$sectionName-$timeStamp$VERSION_FIX$versionNumber$TOTAL_FIX${items.size}$THREE_HASHES"
        if (readString == "" || !readString.startsWith(THREE_HASHES)) {
            return ""
        }

        val withVersionPrefixAndHash = readString.replaceBefore(VERSION_FIX, "") //

        val versionNumberWithPrefix = withVersionPrefixAndHash.replaceAfter(TOTAL_FIX, "")

        val prefixWithTotalPrefix = versionNumberWithPrefix.replace(VERSION_FIX, "")

        return prefixWithTotalPrefix.replace(TOTAL_FIX, "")

    }

    private fun readEntriesCount(readString: String): Int {
        if (readString == "" || !readString.startsWith(THREE_HASHES)) {
            return 0
        }

        val totalSuffixAndCount = readString.replaceBefore(TOTAL_FIX, "")

        val versionNumberWithPrefix = totalSuffixAndCount.removeSuffix(THREE_HASHES)

        return versionNumberWithPrefix.removePrefix(TOTAL_FIX).toInt()
    }

    private fun readRegions(): List<RegionEntity> {
        val regionsList = mutableListOf<RegionEntity>()

        try {
            /*
            Current implementation
            0 - id
            1 - name
             */
            regionsSheet.rowIterator().forEach { pointerRow ->
                if (pointerRow.rowNum > 1) {//skip header row - two of them

                    val builtEntityObject = RegionEntity(
                        pointerRow.getCell(0).stringCellValue.toInt(),
                        pointerRow.getCell(1).stringCellValue
                    )
                    
                    regionsList.add(builtEntityObject)

                }

            }

        } catch (exception: Exception) {
            Timber.log(Log.ERROR, "Error reading region object", exception)
            exception.printStackTrace()
        }

        return regionsList

    }

    private fun readMembers(): List<MemberEntity> {

        return loopAndBuildMemberItem(membersSheet) as List<MemberEntity>
    }

    private fun loopAndBuildMemberItem(providedSheet: Sheet): List<BaseEntity> {

        val readList = mutableListOf<BaseEntity>()

        try {
            providedSheet.rowIterator().forEach { pointerRow ->
                /*
                Current format: Member Object

                columnIndices:
                0 - birthYear
                1 - firstName
                2 - id
                3 - isActive
                4 - isMarried
                5 - otherNames
                6 - phoneNumber
                7 - regionId
                8 - secondName
                9 - sex

                RegionObject:
                0 - id
                1 - name
                 */

                if (pointerRow.rowNum > 1) {//skip header row

                    val cellIterator = pointerRow.cellIterator()
                    
                    val builtEntityObject = MemberEntity(
                        pointerRow.getCell(2).stringCellValue.toInt(),
                        pointerRow.getCell(1).stringCellValue,
                        pointerRow.getCell(9).stringCellValue,
                        pointerRow.getCell(6).stringCellValue,
                        pointerRow.getCell(3).stringCellValue,
                        pointerRow.getCell(10).stringCellValue.toInt(),
                        pointerRow.getCell(0).stringCellValue.toInt(),
                        pointerRow.getCell(5).stringCellValue.toBoolean(),
                        pointerRow.getCell(7).stringCellValue,
                        pointerRow.getCell(4).stringCellValue.toBoolean(),
                        pointerRow.getCell(8).stringCellValue.toInt()
                    )
                    
                    readList.add(builtEntityObject)
                    
                }

            }
        } catch (exception: Exception) {
            Timber.log(Log.ERROR, "Error parsing import member object", exception)
            exception.printStackTrace()
        }

        return readList
    }
}