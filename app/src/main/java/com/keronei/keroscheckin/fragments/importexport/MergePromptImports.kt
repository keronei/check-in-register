package com.keronei.keroscheckin.fragments.importexport

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import cn.pedant.SweetAlert.SweetAlertDialog
import com.keronei.domain.entities.RegionEntity
import com.keronei.keroscheckin.R
import com.keronei.keroscheckin.databinding.FragmentMergeImportsBinding
import com.keronei.keroscheckin.viewmodels.ImportExportViewModel
import com.keronei.keroscheckin.viewmodels.MemberViewModel
import com.keronei.keroscheckin.viewmodels.RegionViewModel
import com.keronei.utils.ToastUtils
import com.keronei.utils.updateRegionIDForMember
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MergePromptImports : Fragment() {

    private lateinit var importBindingOptions: FragmentMergeImportsBinding

    private val importExportViewModel: ImportExportViewModel by activityViewModels()

    private val regionsViewModel: RegionViewModel by activityViewModels()

    private val memberViewModel: MemberViewModel by activityViewModels()

    private lateinit var processingDialog: SweetAlertDialog

    private var headerMessage = ""


    @Inject
    lateinit var coroutineScope: CoroutineScope

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        importBindingOptions =
            DataBindingUtil.inflate(inflater, R.layout.fragment_merge_imports, container, false)

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

        headerMessage = getString(
            R.string.merge_header_message,
            newRegionsText,
            newMembersText,
            existingRegionsText,
            existingMembersText
        )

        importBindingOptions.summaryHeading.text = headerMessage
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
            Timber.log(Log.INFO, "Choose merge option in import; $headerMessage")
            coroutineScope.launch {
                insertNewImports()
            }

        }

    }


    private suspend fun insertNewImports() {
        val totalImportedRegions = MutableStateFlow(value = mutableListOf<Deferred<Long>>())
        val totalImportedMembers = MutableStateFlow(value = mutableListOf<Long>())

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

                coroutineScope {
                    val insertedRegionsId =
                        async {
                            regionsViewModel.createRegion(
                                RegionEntity(
                                    0,
                                    importedRegion.name
                                )
                            )
                        }

                    totalImportedRegions.value.add(insertedRegionsId)

                    val membersOfImportedRegion =
                        async {
                            importExportViewModel.parsedMembersToImport.value.filter { memberEntity ->
                                memberEntity.regionId == importedRegion.id
                            }
                        }


                    val updatedMembersWithLatestRegionIds =
                        async {
                            membersOfImportedRegion.await().map { memberEntity ->
                                updateRegionIDForMember(memberEntity, insertedRegionsId.await())

                            }
                        }

                    //only start feeding members when regions are done for dependency
                    val insertedMembersIds =
                        async { memberViewModel.createNewMember(updatedMembersWithLatestRegionIds.await()) }

                    totalImportedMembers.value.addAll(insertedMembersIds.await())

                }

            }


            cleanUpDuplicateRegionsAlgo()

            withContext(Dispatchers.Main) {
                processingDialog.dismissWithAnimation()
            }

        } catch (exception: Exception) {
            Timber.e(exception)

            withContext(Dispatchers.Main) {
                processingDialog.dismissWithAnimation()

                ToastUtils.showShortToast(R.string.unable_to_import_new_entries)
                findNavController().popBackStack()
            }
        }

        withContext(Dispatchers.Main) {
            processingDialog.dismissWithAnimation()

            val regionsText = resources.getQuantityString(
                R.plurals.regions_prefix,
                totalImportedRegions.value.size,
                totalImportedRegions.value.size
            )
            val membersText = resources.getQuantityString(
                R.plurals.members_prefix,
                totalImportedMembers.value.size,
                totalImportedMembers.value.size
            )

            if (totalImportedMembers.value.isNotEmpty() || totalImportedRegions.value.isNotEmpty()) {
                val successInsertionMessage = getString(
                    R.string.success_import_operation_message,
                    regionsText,
                    membersText
                )
                ToastUtils.showLongToast(
                    successInsertionMessage
                )

                Timber.log(Log.INFO, "Success in insertion; $successInsertionMessage")

                findNavController().popBackStack(R.id.membersFragment, false)
            } else {
                ToastUtils.showShortToast(R.string.unable_to_import_new_entries)
                findNavController().popBackStack()
                Timber.log(Log.WARN, "Failed to import; $headerMessage")
            }
        }

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

                Timber.log(
                    Log.INFO, "Choose replace option in import with conflict; $headerMessage"
                )
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