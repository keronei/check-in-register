package com.keronei.keroscheckin.fragments.importexport

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.keronei.android.common.Constants
import com.keronei.domain.entities.MemberEntity
import com.keronei.domain.entities.RegionEntity
import com.keronei.keroscheckin.R
import com.keronei.keroscheckin.databinding.DialogImportExportLayoutBinding
import com.keronei.keroscheckin.preference.SELECTION
import com.keronei.keroscheckin.viewmodels.MemberViewModel
import com.keronei.keroscheckin.viewmodels.RegionViewModel
import com.keronei.utils.ToastUtils
import com.keronei.utils.export.ExportRegionMembersProcessor
import com.keronei.utils.import.ImportRegionMembersProcessor
import com.keronei.utils.makeShareIntent
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

    lateinit var binding: DialogImportExportLayoutBinding

    private val regionsViewModel: RegionViewModel by activityViewModels()

    private val memberViewModel: MemberViewModel by activityViewModels()

    private var displayedPrompt: androidx.appcompat.app.AlertDialog? = null

    private var selection = SELECTION.UNSELECTED

    lateinit var getContent: ActivityResultLauncher<String>

    private lateinit var processingDialog: SweetAlertDialog

    @Inject
    lateinit var coroutineScope: CoroutineScope

    private fun registerContract() {
        //TODO use lifeCycleScope to watch
        getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri ->
            val result = requireContext().contentResolver.openInputStream(uri)

            handleImportFileSelection(result!!)
        }
    }

    private fun unregisterContract() {
        getContent.unregister()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        processingDialog = SweetAlertDialog(requireContext(), SweetAlertDialog.PROGRESS_TYPE)
        registerContract()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        unregisterContract()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.dialog_import_export_layout,
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
                            Constants.EXPORT_FILE_NAME,
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

                    launchSendData(sendingIntent, summary)
                }

            } catch (exception: Exception) {

                processingDialog.dismissWithAnimation()

                showErrorDialog(
                    getString(R.string.unable_to_export_title),
                    getString(R.string.unable_to_export_message)
                )

                exception.printStackTrace()
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

    private fun launchSendData(sendDataIntent: Intent, summary: String) {
        try {

            SweetAlertDialog(requireContext(), SweetAlertDialog.BUTTON_CONFIRM)
                .setTitleText(getString(R.string.summary_string))
                .setContentText(
                    summary
                )
                .setConfirmButton(getString(R.string.export_option)) { confirmationPrompt ->

                    confirmationPrompt.dismissWithAnimation()

                    sendDataIntent.action = Intent.ACTION_SEND
                    startActivity(
                        Intent.createChooser(
                            sendDataIntent,
                            getString(R.string.export_regions_members)
                        )
                    )
                }.show()

        } catch (exception: java.lang.Exception) {
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

        //this.dismiss()

        if (overview.appVersion == "" || overview.membersEntrySize == 0) {
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

                        Timber.d("Parsed result $result")

                        withContext(Dispatchers.Main) {
                            processingDialog.dismissWithAnimation()

                            confirmationPrompt.dismissWithAnimation()

                            promptLocalContentRetention(result.keys.first(), result.values.first())

                        }
                    }
                } catch (exception: Exception) {
                    exception.printStackTrace()
                    processingDialog.dismissWithAnimation()

                    confirmationPrompt.dismissWithAnimation()

                    SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE).setContentText(
                        exception.message ?: "Something unexpected happened."
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
    ) {
        val regions = runBlocking {
            regionsViewModel.queryAllRegions().first()
        }


        val members = runBlocking { memberViewModel.queryAllMembers().first() }

        if (members.size < 2 || regions.size < 2) {
            cleanUpAndAddImports(readRegionsList, readMembersList)
        } else {
            val proceedToOptions = MergePromptImports()
            proceedToOptions.show(requireActivity().supportFragmentManager, MergePromptImports.TAG)

        }
        this.dismiss()

    }

    private fun cleanUpAndAddImports(
        readRegionsList: List<RegionEntity>,
        readMembersList: List<MemberEntity>
    ) {
        coroutineScope.launch {

            //just re-query and delete all
            val existingRegions = regionsViewModel.queryAllRegions().first()

            memberViewModel.deleteAllMembers()

            regionsViewModel.deleteAllRegions(existingRegions)

        }

    }

    companion object {
        const val TAG = "ImportExportBottomSheet"
    }


}