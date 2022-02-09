package com.keronei.utils.import


import android.util.Log
import com.keronei.android.common.Constants
import com.keronei.android.common.Constants.THREE_HASHES
import com.keronei.android.common.Constants.TOTAL_FIX
import com.keronei.domain.entities.BaseEntity
import com.keronei.domain.entities.MemberEntity
import com.keronei.domain.entities.RegionEntity
import com.keronei.keroscheckin.models.ImportSummary
import com.keronei.utils.export.EntityType
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
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
            ioException.printStackTrace()
        } catch (exception: Exception) {
            exception.printStackTrace()
        } finally {
            try {
                fileInputStream?.close()
            } catch (exception: Exception) {
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
        return readString.removeSuffix(Constants.VERSION_FIX, TOTAL_FIX)

    }

    private fun readEntriesCount(readString: String): Int {
        val result = readString.removeSurrounding(TOTAL_FIX, THREE_HASHES)

        return result.toInt()
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

                    val cellIterator = pointerRow.cellIterator()

                    while (cellIterator.hasNext()) {

                        val builtEntityObject = RegionEntity(
                            pointerRow.getCell(0).stringCellValue.toInt(),
                            pointerRow.getCell(1).stringCellValue
                        )

                        regionsList.add(builtEntityObject)
                    }
                }

            }

        } catch (exception: Exception) {
            exception.printStackTrace()
        }

        return regionsList

    }

    private fun readMembers(): List<MemberEntity> {

        return loopAndBuildItem(membersSheet, EntityType.MEMBER) as List<MemberEntity>
    }

    private fun loopAndBuildItem(providedSheet: Sheet, modelToBuild: EntityType): List<BaseEntity> {

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
                4 - otherNames
                5 - phoneNumber
                6 - regionId
                7 - secondName
                8 - sex

                RegionObject:
                0 - id
                1 - name
                 */

                if (pointerRow.rowNum > 1) {//skip header row

                    val cellIterator = pointerRow.cellIterator()

                    while (cellIterator.hasNext()) {

                        val builtEntityObject = MemberEntity(
                            pointerRow.getCell(2).stringCellValue.toInt(),
                            pointerRow.getCell(1).stringCellValue,
                            pointerRow.getCell(7).stringCellValue,
                            pointerRow.getCell(4).stringCellValue,
                            pointerRow.getCell(8).stringCellValue.toInt(),
                            pointerRow.getCell(0).stringCellValue.toInt(),
                            pointerRow.getCell(5).stringCellValue,
                            pointerRow.getCell(3).stringCellValue.toBoolean(),
                            pointerRow.getCell(6).stringCellValue.toInt()
                        )

                        readList.add(builtEntityObject)

                        Log.d("Reading from excel", "$builtEntityObject")
                    }

                }
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
        }

        return readList
    }
}