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
import java.io.InputStream
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

@AndroidEntryPoint
class ImportExportSheet : BottomSheetDialogFragment() {

    lateinit var binding: DialogImportExportLayoutBinding

    private val regionsViewModel: RegionViewModel by activityViewModels()

    private val memberViewModel: MemberViewModel by activityViewModels()

    private var displayedPrompt: androidx.appcompat.app.AlertDialog? = null

    private var selection = SELECTION.UNSELECTED

    lateinit var getContent: ActivityResultLauncher<String>

    @Inject
    lateinit var coroutineScope: CoroutineScope

    private fun registerContract() {
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
        val regions = runBlocking {
            regionsViewModel.queryAllRegions().first()
        }


        val members = runBlocking { memberViewModel.queryAllMembers().first() }


        //don't prepare workbook if it's only guest region with no members.

        if (regions.size < 2 && members.isEmpty()) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.emty_export))
                .setMessage(getString(R.string.empty_export_message))
                .setPositiveButton(getString(R.string.dialog_cancel)) { dialog, _ ->
                    dialog.dismiss()
                    displayedPrompt?.dismiss()
                }
                .show()
            return
        }


        val preparedWorkBook = ExportRegionMembersProcessor(
            regions,
            members,
            Calendar.getInstance().timeInMillis,
            getString(R.string.version)
        ).createExportFile()

        val sendingIntent =
            makeShareIntent(Constants.EXPORT_FILE_NAME, preparedWorkBook, requireContext())

        val totalRegions = regions.size - 1//minus guest region

        val regionsPrefix =
            if (regions.size < 2) getString(R.string.guest_region) else resources.getQuantityString(
                R.plurals.regions_prefix,
                totalRegions,
                totalRegions
            )

        val membersPrefix =
            resources.getQuantityString(R.plurals.members_prefix, members.size, members.size)

        val summary = getString(
            R.string.summary_export,
            regionsPrefix,
            membersPrefix
        )

        launchSendData(sendingIntent, summary)

        displayedPrompt?.dismiss()

    }

    private fun launchSendData(sendDataIntent: Intent, summary: String) {
        try {

            MaterialAlertDialogBuilder(requireContext())
                .setMessage(summary)
                .setNegativeButton(getString(R.string.dialog_cancel)) { dialog, _ ->
                    dialog.dismiss()
                }.setPositiveButton(getString(R.string.export_option)) { _, _ ->
                    sendDataIntent.action = Intent.ACTION_SEND
                    startActivity(
                        Intent.createChooser(
                            sendDataIntent,
                            getString(R.string.export_regions_members)
                        )
                    )

                }
                .show()


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

        this.dismiss()

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
            .setConfirmButton(getString(R.string.proceed)) {

                val processingDialog =
                    SweetAlertDialog(requireContext(), SweetAlertDialog.PROGRESS_TYPE)

                try {
                   processingDialog.show()

                    coroutineScope.launch {
                        val result = waitForResults(importRegionMembersProcessor)

                        withContext(Dispatchers.Main) {
                            processingDialog.dismissWithAnimation()

                            promptLocalContentRetention(result.keys.first(), result.values.first())
                        }
                    }
                } catch (exception: Exception) {
                    exception.printStackTrace()
                    processingDialog.dismissWithAnimation()

                    SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE).setContentText(
                        exception.message ?: "Something unexpected happened."
                    ).show()

                }


            }.show()
    }

    private suspend fun waitForResults(importRegionMembersProcessor: ImportRegionMembersProcessor):
            HashMap<List<RegionEntity>, List<MemberEntity>> {

        val actualDataEntriesJob = coroutineScope.async {
           return@async importRegionMembersProcessor.readEntries()
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

        val proceedToOptions = MergePromptImports()

        proceedToOptions.show(childFragmentManager, MergePromptImports.TAG)

    }

    companion object {
        const val TAG = "ImportExportBottomSheet"
    }


}