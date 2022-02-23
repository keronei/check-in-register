package com.keronei.keroscheckin.fragments.importexport

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import cn.pedant.SweetAlert.SweetAlertDialog
import com.keronei.domain.entities.MemberEntity
import com.keronei.domain.entities.RegionEntity
import com.keronei.keroscheckin.R
import com.keronei.keroscheckin.databinding.BottomModalMergeImportsBinding
import com.keronei.keroscheckin.viewmodels.ImportExportViewModel
import com.keronei.keroscheckin.viewmodels.MemberViewModel
import com.keronei.keroscheckin.viewmodels.RegionViewModel
import com.keronei.utils.ToastUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@AndroidEntryPoint
class MergePromptImports : Fragment() {

    lateinit var importBindingOptions: BottomModalMergeImportsBinding

    private val importExportViewModel: ImportExportViewModel by activityViewModels()

    private val regionsViewModel: RegionViewModel by activityViewModels()

    private val memberViewModel: MemberViewModel by activityViewModels()

    private lateinit var processingDialog: SweetAlertDialog


    @Inject
    lateinit var coroutineScope: CoroutineScope

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        importBindingOptions =
            DataBindingUtil.inflate(inflater, R.layout.bottom_modal_merge_imports, container, false)

        processingDialog = SweetAlertDialog(requireContext(), SweetAlertDialog.PROGRESS_TYPE)

        setOnclickListeners()

        setConflictHeaderMessage()

        return importBindingOptions.root
    }

    private fun setConflictHeaderMessage() {
        val newRegion = importExportViewModel.parsedRegionsToImport.value
        val newMembers = importExportViewModel.parsedMembersToImport.value

        val existingRegions = runBlocking { regionsViewModel.queryAllRegions().first() }
        val existingMembers = runBlocking { memberViewModel.queryAllMembers().first() }

        val newRegionsText =
            resources.getQuantityString(R.plurals.regions_prefix, newRegion.size, newRegion.size)
        val existingRegionsText = resources.getQuantityString(
            R.plurals.regions_prefix,
            existingRegions.size,
            existingRegions.size
        )

        val newMembersText =
            resources.getQuantityString(R.plurals.members_prefix, newMembers.size, newMembers.size)
        val existingMembersText = resources.getQuantityString(
            R.plurals.members_prefix,
            existingMembers.size,
            existingMembers.size
        )

        importBindingOptions.summaryHeading.text = getString(
            R.string.merge_header_message,
            newRegionsText,
            newMembersText,
            existingRegionsText,
            existingMembersText
        )
    }


    private fun setOnclickListeners() {
        importBindingOptions.imgBtnCloseMergeOptions.setOnClickListener {
            findNavController().popBackStack()
        }
        importBindingOptions.replace.setOnClickListener {
            processingDialog.show()
            cleanUpLocalDataAndAddNew()
        }

        importBindingOptions.merge.setOnClickListener {
            processingDialog.show()
            coroutineScope.launch {
                insertNewImports()
            }

        }

    }


    private suspend fun insertNewImports() {
        val totalImportedRegions = mutableListOf<Int>()
        val totalImportedMembers = mutableListOf<Int>()

        /**
         * Since these are related entries, creating an association before insertion
         * will guarantee matches in the resulting list.
         *
         * Step 1: Group all the regions with their members
         * Step 2: Insert in batches while associating dependencies correctly
         *
         *
         */

        try {

            importExportViewModel.parsedRegionsToImport.value.forEach { importedRegion ->

                val insertedRegionsIds =
                    regionsViewModel.createRegion(listOf(RegionEntity(0, importedRegion.name)))

                totalImportedRegions.addAll(insertedRegionsIds.map { generatedId -> generatedId.toInt() })


                val membersOfImportedRegion =
                    importExportViewModel.parsedMembersToImport.value.filter { memberEntity ->
                        memberEntity.regionId == importedRegion.id
                    }

                val updatedMembersWithLatestRegionIds =
                    membersOfImportedRegion.map { memberEntity ->
                        updateRegionIDForMember(memberEntity, insertedRegionsIds.first())

                    }

                //only start feeding members when regions are done for dependency
                val insertedMembersIds =
                    memberViewModel.createNewMember(updatedMembersWithLatestRegionIds)

                totalImportedMembers.addAll(insertedMembersIds.map { createdId -> createdId.toInt() })

            }

            cleanUpDuplicateRegionsAlgo()

            withContext(Dispatchers.Main) {
                processingDialog.dismissWithAnimation()
            }

        } catch (exception: Exception) {
            withContext(Dispatchers.Main) {
                processingDialog.dismissWithAnimation()

                ToastUtils.showShortToast(R.string.unable_to_import_new_entries)
                findNavController().popBackStack()
                exception.printStackTrace()
            }
        }

        withContext(Dispatchers.Main) {
            processingDialog.dismissWithAnimation()
            if (totalImportedMembers.isNotEmpty() || totalImportedRegions.isNotEmpty()) {
                ToastUtils.showLongToast(
                    getString(
                        R.string.success_import_operation_message,
                        totalImportedRegions.size,
                        totalImportedMembers.size
                    )
                )

                findNavController().popBackStack(R.id.membersFragment, false)
            } else {
                ToastUtils.showShortToast(R.string.unable_to_import_new_entries)
                findNavController().popBackStack()
            }
        }

    }

    private fun updateRegionIDForMember(member: MemberEntity, id: Long): MemberEntity {
        return MemberEntity(
            member.id,
            member.firstName,
            member.secondName,
            member.otherNames,
            member.sex,
            member.birthYear,
            member.isMarried,
            member.phoneNumber,
            member.isActive,
            id.toInt()
        )
    }

    //TODO find duplicates in names and remove those without members
    private fun cleanUpDuplicateRegionsAlgo() {}

    private fun cleanUpLocalDataAndAddNew() {
        coroutineScope.launch {
            try {
                //just re-query and delete all
                val existingRegions = regionsViewModel.queryAllRegions().first()

                memberViewModel.deleteAllMembers()

                regionsViewModel.deleteAllRegions(existingRegions)

                insertNewImports()
            } catch (exception: Exception) {
                withContext(Dispatchers.Main) {
                    processingDialog.dismissWithAnimation()
                    ToastUtils.showShortToast(R.string.unable_to_import_new_entries)
                    findNavController().popBackStack()
                    exception.printStackTrace()
                }
            }

        }
    }

    companion object {
        const val TAG = "MergePromptImports"
    }
}