package com.keronei.keroscheckin.fragments.importexport

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.keronei.android.common.Constants.EXPORT_FILE_NAME
import com.keronei.domain.entities.MemberEntity
import com.keronei.domain.entities.RegionEntity
import com.keronei.keroscheckin.R
import com.keronei.keroscheckin.databinding.FragmentImportExportLayoutBinding
import com.keronei.keroscheckin.preference.SELECTION
import com.keronei.keroscheckin.viewmodels.ImportExportViewModel
import com.keronei.keroscheckin.viewmodels.MemberViewModel
import com.keronei.keroscheckin.viewmodels.RegionViewModel
import com.keronei.utils.ToastUtils
import com.keronei.utils.export.ExportRegionMembersProcessor
import com.keronei.utils.import.ImportRegionMembersProcessor
import com.keronei.utils.makeShareIntent
import com.keronei.utils.updateRegionIDForMember
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import timber.log.Timber
import java.io.InputStream
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class ImportExportSheet : BottomSheetDialogFragment() {

    lateinit var binding: FragmentImportExportLayoutBinding

    private val regionsViewModel: RegionViewModel by activityViewModels()

    private val memberViewModel: MemberViewModel by activityViewModels()

    private val importExportViewModel: ImportExportViewModel by activityViewModels()

    private var displayedPrompt: androidx.appcompat.app.AlertDialog? = null

    private var selection = SELECTION.UNSELECTED

    lateinit var getContent: ActivityResultLauncher<String>

    lateinit var createExportFileContract: ActivityResultLauncher<Intent>

    private lateinit var processingDialog: SweetAlertDialog

    private lateinit var hssfWorkbookToWriteOut: HSSFWorkbook

    @Inject
    lateinit var coroutineScope: CoroutineScope

    private fun registerContract() {
        //TODO use lifeCycleScope to watch
        getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->

            if (uri != null) {
                val result = requireContext().contentResolver.openInputStream(uri)
                if (result != null) {
                    handleImportFileSelection(result)
                    displayedPrompt?.dismiss()
                } else {
                    ToastUtils.showShortToast(getString(R.string.did_not_select_file))
                }
            } else {
                ToastUtils.showShortToast(getString(R.string.did_not_select_file))
            }
        }


    }

    private fun registerContractForCreatingExportFile() {
        createExportFileContract =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { createdFileUri ->
                if (this::hssfWorkbookToWriteOut.isInitialized) {
                    if (createdFileUri?.data?.data != null) {
                        try {
                            val fileOutputStream =
                                requireContext().contentResolver.openOutputStream(createdFileUri.data!!.data!!)
                            hssfWorkbookToWriteOut.write(fileOutputStream)
                            Timber.log(Log.INFO, "Successfully loaded data import file.")
                        } catch (exception: Exception) {
                            Timber.log(
                                Log.ERROR,
                                "Something went wrong in creating export file",
                                exception
                            )
                            ToastUtils.showShortToast(getString(R.string.failed_write_out))
                        }
                    } else {
                        ToastUtils.showShortToast(getString(R.string.null_uri_message_to_user_save_file))
                    }
                } else {
                    ToastUtils.showShortToast(getString(R.string.workbok_no_initialized_message))
                }
            }
    }

    private fun unregisterContract() {
        getContent.unregister()

        createExportFileContract.unregister()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        processingDialog = SweetAlertDialog(requireContext(), SweetAlertDialog.PROGRESS_TYPE)
        registerContract()

        registerContractForCreatingExportFile()

    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        unregisterContract()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //all is settled. check if there's any data to process
        val sentData = importExportViewModel.launchedIntentInputStream.value

        if (sentData != null) {

            handleImportFileSelection(sentData)

            displayedPrompt?.dismiss()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_import_export_layout,
            container,
            false
        )

        initViews()

        return binding.root
    }

    private fun initViews() {
        with(binding) {
            btnNext.setOnClickListener {
                when (selection) {
                    SELECTION.UNSELECTED -> ToastUtils.showLongToast(getString(R.string.make_selection_import_export))

                    SELECTION.IMPORT -> importData()

                    SELECTION.EXPORT -> exportData()
                }
            }

            importCard.setOnClickListener {

                importCard.isChecked = !importCard.isChecked

                if (importCard.isChecked) {
                    exportCard.isChecked = !importCard.isChecked
                    selection = SELECTION.IMPORT
                } else {
                    selection = SELECTION.UNSELECTED
                }
            }

            exportCard.setOnClickListener {

                exportCard.isChecked = !exportCard.isChecked

                if (exportCard.isChecked) {
                    importCard.isChecked = !exportCard.isChecked
                    selection = SELECTION.EXPORT
                } else {
                    selection = SELECTION.UNSELECTED
                }

            }
        }
    }

    private fun exportData() {
        processingDialog.show()

        val regions = runBlocking {
            regionsViewModel.queryAllRegions().first()
        }

        val members = runBlocking { memberViewModel.queryAllMembers().first() }

        coroutineScope.launch {
            //don't prepare workbook if it's only guest region with no members.
            processingDialog.dismissWithAnimation()

            withContext(Dispatchers.Main) {
                if (regions.size < 2 && members.isEmpty()) {
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle(getString(R.string.empty_export))
                        .setMessage(getString(R.string.empty_export_message))
                        .setPositiveButton(getString(R.string.dialog_cancel)) { dialog, _ ->
                            dialog.dismiss()
                            displayedPrompt?.dismiss()
                        }
                        .show()
                    return@withContext
                }

            }

            processingDialog.show()

            try {
                val preparedWorkBook = createExportFile(regions, members)

                withContext(Dispatchers.Main) {

                    processingDialog.dismissWithAnimation()

                    val sendingIntent =
                        makeShareIntent(
                            EXPORT_FILE_NAME,
                            preparedWorkBook,
                            requireContext()
                        )

                    val totalRegions = regions.size - 1//minus guest region

                    val regionsPrefix =
                        if (regions.size < 2) getString(R.string.guest_region) else resources.getQuantityString(
                            R.plurals.regions_prefix,
                            totalRegions,
                            totalRegions
                        )

                    val membersPrefix =
                        resources.getQuantityString(
                            R.plurals.members_prefix,
                            members.size,
                            members.size
                        )

                    val summary = getString(
                        R.string.summary_export,
                        regionsPrefix,
                        membersPrefix
                    )

                    Timber.log(Log.INFO, "At exporting data : $summary")

                    launchSendData(sendingIntent, preparedWorkBook, summary)
                }

            } catch (exception: Exception) {

                processingDialog.dismissWithAnimation()

                showErrorDialog(
                    getString(R.string.unable_to_export_title),
                    getString(R.string.unable_to_export_message)
                )

                Timber.log(
                    Log.ERROR,
                    "Failed to export ${regions.size} regions and ${members.size} members.",
                    exception
                )
            }

        }

        displayedPrompt?.dismiss()

    }

    private suspend fun createExportFile(
        regions: List<RegionEntity>,
        members: List<MemberEntity>
    ): HSSFWorkbook {
        val preparedWorkBook = coroutineScope.async {
            ExportRegionMembersProcessor(
                regions,
                members,
                Calendar.getInstance().timeInMillis,
                getString(R.string.version)
            ).createExportFile()
        }

        return preparedWorkBook.await()
    }

    private fun launchSendData(
        sendDataIntent: Intent,
        preparedWorkBook: HSSFWorkbook,
        summary: String
    ) {
        try {

            SweetAlertDialog(requireContext(), SweetAlertDialog.BUTTON_CONFIRM)
                .setTitleText(getString(R.string.summary_string))
                .setContentText(
                    summary
                )
                .setConfirmButton(getString(R.string.export_option)) { confirmationPrompt ->

                    confirmationPrompt.dismissWithAnimation()

                    Timber.log(Log.INFO, "Preferred to send export file : $summary ")


                    sendDataIntent.action = Intent.ACTION_SEND
                    startActivity(
                        Intent.createChooser(
                            sendDataIntent,
                            getString(R.string.export_regions_members)
                        )
                    )
                }
                .setNeutralButton(getString(R.string.save_option_export)) { alertDialog ->

                    val dataUri = sendDataIntent.data

                    hssfWorkbookToWriteOut = preparedWorkBook

                    Timber.log(Log.INFO, "Preferred to save export file : $summary ")

                    val saveOptionIntent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                        addCategory(Intent.CATEGORY_OPENABLE)
                        type = "application/vnd.ms-excel"
                        putExtra(Intent.EXTRA_TITLE, EXPORT_FILE_NAME)

                        putExtra(Intent.EXTRA_STREAM, dataUri)
                    }

                    alertDialog.dismissWithAnimation()

                    createExportFileContract.launch(saveOptionIntent)
                }

                .show()

        } catch (exception: Exception) {
            Timber.log(Log.ERROR, "Attempting to export data : $summary", exception)
            SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                .setTitleText(getString(R.string.dialog_error_title))
                .setContentText(
                    getString(
                        R.string.dialog_error_template, exception.message
                    )
                )
                .show()
        }
    }

    private fun importData() {
        getContent.launch("application/vnd.ms-excel")

        displayedPrompt?.dismiss()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun handleImportFileSelection(file: InputStream) {
        val importRegionMembersProcessor = ImportRegionMembersProcessor(file)

        val overview = importRegionMembersProcessor.readBasicInformation() //.readEntries()

        if (overview.appVersion == "" || overview.membersEntrySize == 0) {
            Timber.log(
                Log.WARN,
                "Could not process import because app version is empty and there's no member."
            )
            showErrorDialog(
                getString(R.string.unable_to_import_title),
                getString(R.string.import_error_message)
            )
            return
        }

        if (overview.appVersion != getString(R.string.version)) {
            val versionMismatchInfo = getString(
                R.string.app_version_mismatch_import,
                getString(R.string.version),
                overview.appVersion
            )

            Timber.log(
                Log.WARN,
                "Failed to import data because data is from ${overview.appVersion} and user is in ${
                    getString(R.string.version)
                } (versions)."
            )

            SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText(getString(R.string.version_mismatch_title))
                .setContentText(versionMismatchInfo)
                .show()
            return
        }

        val membersString = resources.getQuantityString(
            R.plurals.members_prefix,
            overview.membersEntrySize,
            overview.membersEntrySize
        )

        val regionsString = resources.getQuantityString(
            R.plurals.regions_prefix,
            overview.regionsEntrySize,
            overview.regionsEntrySize
        )

        Timber.log(
            Log.INFO, "To import data, has preview - ${
                getString(
                    R.string.summary_import_confirmation_text,
                    regionsString,
                    membersString
                )
            }"
        )

        SweetAlertDialog(requireContext(), SweetAlertDialog.BUTTON_CONFIRM)
            .setTitleText(getString(R.string.summary_string))
            .setContentText(
                getString(
                    R.string.summary_import_confirmation_text,
                    regionsString,
                    membersString
                )
            )
            .setConfirmButton(getString(R.string.proceed)) { confirmationPrompt ->


                try {
                    processingDialog.show()

                    coroutineScope.launch {
                        val result = waitForResults(importRegionMembersProcessor)

                        Timber.log(
                            Log.INFO,
                            "Import processor parsed ${result.keys.size} regions and ${result.values.size} members."
                        )

                        withContext(Dispatchers.Main) {
                            processingDialog.dismissWithAnimation()

                            confirmationPrompt.dismissWithAnimation()

                            promptLocalContentRetention(result.keys.first(), result.values.first())

                            //reset if this was via open with
                            importExportViewModel.launchedIntentInputStream.value = null

                        }
                    }
                } catch (exception: Exception) {
                    Timber.log(
                        Log.ERROR, "Could not parse import file, but preview had this : ${
                            getString(
                                R.string.summary_import_confirmation_text,
                                regionsString,
                                membersString
                            )
                        }", exception
                    )
                    processingDialog.dismissWithAnimation()

                    confirmationPrompt.dismissWithAnimation()

                    SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE).setContentText(
                        exception.message
                            ?: getString(R.string.unable_to_import_found_entries_message)
                    ).show()

                }


            }.show()
    }

    private fun showErrorDialog(title: String, message: String) {
        SweetAlertDialog(
            requireContext(),
            SweetAlertDialog.ERROR_TYPE
        ).setContentText(message)
            .setTitleText(title)
            .setCancelButton(getString(R.string.close_dialog)) { dialog ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showInfoDialog(title: String, message: String) {
        SweetAlertDialog(
            requireContext(),
            SweetAlertDialog.NORMAL_TYPE
        ).setContentText(message)
            .setTitleText(title)
            .setCancelButton(getString(R.string.close_dialog)) { dialog ->
                dialog.dismiss()
            }
            .show()
    }

    private suspend fun waitForResults(importRegionMembersProcessor: ImportRegionMembersProcessor):
            HashMap<List<RegionEntity>, List<MemberEntity>> {

        val actualDataEntriesJob = coroutineScope.async {
            importRegionMembersProcessor.readEntries()
        }

        return actualDataEntriesJob.await()
    }

    private fun promptLocalContentRetention(
        readRegionsList: List<RegionEntity>,
        readMembersList: List<MemberEntity>
    ) {//TODO remove runBlocking and use async
        processingDialog?.show()
        coroutineScope.launch {

            val regions = async {
                regionsViewModel.queryAllRegions().first()
            }


            val members = async { memberViewModel.queryAllMembers().first() }

            withContext(Dispatchers.Main) {

                val allRegions = regions.await()
                val allMembers = members.await()

                processingDialog?.dismissWithAnimation()

                importExportViewModel.parsedRegionsToImport.value =
                    readRegionsList as MutableList<RegionEntity>

                importExportViewModel.parsedMembersToImport.value =
                    readMembersList as MutableList<MemberEntity>

                if (allMembers.size < 2 || allRegions.size < 2) {
                    processingDialog.show()

                    cleanUpAndAddImports(readRegionsList, readMembersList)
                } else {
                    findNavController().navigate(R.id.action_settingsFragment_to_mergePromptImports)
                        .also { this@ImportExportSheet.dismiss() }
                }
            }

        }
    }

    private fun cleanUpAndAddImports(
        readRegionsList: List<RegionEntity>,
        readMembersList: List<MemberEntity>
    ) {
        coroutineScope.launch {

            try {
                //just re-query and delete all
                val existingRegions = regionsViewModel.queryAllRegions().first()

                memberViewModel.deleteAllMembers()

                regionsViewModel.deleteAllRegions(existingRegions)

                val idsOfCreatedRegions = mutableListOf<Long>()
                val idsOfCreatedMembers = mutableListOf<Long>()

                readRegionsList.forEach { readRegionEntry ->
                    val createdRegionId = regionsViewModel.createRegion(readRegionEntry)

                    idsOfCreatedRegions.add(createdRegionId)

                    val readMembersUnderCreatedRegion =
                        readMembersList.filter { memberEntity -> memberEntity.regionId == readRegionEntry.id }

                    val membersWithUpdatedRegionIds =
                        readMembersUnderCreatedRegion.map { memberEntity ->
                            updateRegionIDForMember(
                                memberEntity,
                                createdRegionId
                            )
                        }

                    val finalMembersAfterAdd =
                        memberViewModel.createNewMember(membersWithUpdatedRegionIds)

                    idsOfCreatedMembers.addAll(finalMembersAfterAdd)

                }

                withContext(Dispatchers.Main) {
                    processingDialog.dismissWithAnimation()

                    if (idsOfCreatedMembers.isNotEmpty() || idsOfCreatedRegions.isNotEmpty()) {

                        val membersCountString = resources.getQuantityString(
                            R.plurals.members_prefix,
                            idsOfCreatedMembers.size,
                            idsOfCreatedMembers.size
                        )

                        val regionsCountString = resources.getQuantityString(
                            R.plurals.regions_prefix,
                            idsOfCreatedRegions.size,
                            idsOfCreatedRegions.size
                        )

                        showInfoDialog(
                            getString(R.string.success_import_operation_title),
                            getString(
                                R.string.success_import_operation_message,
                                regionsCountString,
                                membersCountString
                            )

                        )

                        Timber.log(
                            Log.INFO,
                            "Cleaned up local data and performed insertion successfully : ${
                                getString(
                                    R.string.success_import_operation_message,
                                    regionsCountString,
                                    membersCountString
                                )
                            }"
                        )

                        findNavController().popBackStack(R.id.membersFragment, false)

                    } else {

                        Timber.log(
                            Log.WARN, "Failed to perform clean up and insertion" +
                                    " with operation clean instantly, had ${readRegionsList.size} " +
                                    "regions and ${readMembersList.size} members."
                        )
                        showErrorDialog(
                            getString(R.string.failed_operation_dialog_title),
                            getString(R.string.unable_to_import_new_entries)
                        )
                    }

                    this@ImportExportSheet.dismiss()

                }
            } catch (exception: Exception) {
                Timber.log(
                    Log.ERROR,
                    "Could not clean instantly for import; has ${readRegionsList.size}" +
                            " regions and ${readMembersList.size} to import.",
                    exception
                )
                withContext(Dispatchers.Main) {
                    processingDialog.dismissWithAnimation()
                    ToastUtils.showLongToast(getString(R.string.unable_to_import_new_entries))
                    findNavController().popBackStack(R.id.membersFragment, false)
                        .also { this@ImportExportSheet.dismiss() }


                }
            }
        }

    }

    companion object {
        const val TAG = "ImportExportBottomSheet"
    }


}