package com.keronei.keroscheckin.preference

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toolbar
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.guardanis.applock.dialogs.LockCreationDialogBuilder
import com.guardanis.applock.dialogs.UnlockDialogBuilder
import com.keronei.android.common.Constants.TELEGRAM_SUPPORT_GROUP_LINK
import com.keronei.data.repository.mapper.MemberLocalEntityMapper
import com.keronei.domain.entities.MemberEntity
import com.keronei.domain.entities.RegionEntity
import com.keronei.keroscheckin.R
import com.keronei.keroscheckin.fragments.importexport.ImportExportSheet
import com.keronei.keroscheckin.viewmodels.MemberViewModel
import com.keronei.keroscheckin.viewmodels.RegionViewModel
import com.keronei.utils.ToastUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import timber.log.Timber
import javax.inject.Inject

enum class SELECTION {
    EXPORT, IMPORT, UNSELECTED
}

@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat() {

    @Inject
    lateinit var sharedPreference: SharedPreferences


    private val regionsViewModel: RegionViewModel by activityViewModels()

    private val membersViewModel: MemberViewModel by activityViewModels()

    @Inject
    lateinit var memberEntityMapper: MemberLocalEntityMapper


    private lateinit var processingDialog: SweetAlertDialog

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {

        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        processingDialog = SweetAlertDialog(requireContext(), SweetAlertDialog.PROGRESS_TYPE)

        retrieveVersionNumber()

        exportImport()

        joinSupport()

        wipeAllData()

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = view.findViewById<Toolbar>(R.id.prefs_toolbar)

        if (toolbar != null) {
            configureToolBar(toolbar)
            //ToastUtils.showShortToast("Tool found")
        } else {
            //ToastUtils.showShortToast("No Tool ")

        }
    }

    private fun configureToolBar(toolbar: Toolbar) {
        toolbar.setNavigationIcon(R.drawable.ic_navigate_back_24)
        toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun wipeAllData() {
        val exportImport = findPreference<Preference>(getString(R.string.clean_up))

        var checkedItem = 1

        exportImport?.onPreferenceClickListener =
            Preference.OnPreferenceClickListener {

                processingDialog.show()

                val regions = runBlocking { regionsViewModel.queryAllRegions().first() }
                val members = runBlocking { membersViewModel.queryAllMembers().first() }

                val unusedRegions =
                    regions.filter { providedRegion ->
                        providedRegion.id !in members.map { providedMember ->
                            providedMember.regionId
                        }
                    }

                processingDialog.dismissWithAnimation()

                val deletionOptions = arrayOf(
                    resources.getQuantityString(
                        R.plurals.regions_deletion_option,
                        unusedRegions.size,
                        unusedRegions.size
                    ),
                    resources.getQuantityString(
                        R.plurals.members_deletion_option,
                        members.size,
                        members.size
                    ),
                    getString(R.string.delete_all_option)
                )


                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(getString(R.string.wipe_data_dialog_title))
                    .setSingleChoiceItems(
                        deletionOptions,
                        checkedItem
                    ) { _, which ->
                        checkedItem = which
                    }
                    .setPositiveButton(getString(R.string.delete_data_button_text)) { dialog, _ ->
                        dialog.dismiss()

                        if (checkedItem == 0 && unusedRegions.isEmpty()) {
                            ToastUtils.showLongToast(getString(R.string.nothing_to_delete))
                            return@setPositiveButton
                        }

                        if (checkedItem == 1 && members.isEmpty()) {
                            ToastUtils.showLongToast(getString(R.string.nothing_to_delete))
                            return@setPositiveButton
                        }

                        val appLock = UnlockDialogBuilder(requireActivity())

                        appLock
                            .onUnlocked {
                                val regionsToBeDeletedBackup = mutableListOf<RegionEntity>()

                                val membersToBeDeletedBackUp = mutableListOf<MemberEntity>()

                                regionsToBeDeletedBackup.addAll(unusedRegions)
                                membersToBeDeletedBackUp.addAll(members)

                                when (checkedItem) {
                                    0 -> {

                                        try {
                                            /*
                                            Filter those without associations then delete them.
                                             */

                                            Timber.d("Deletable regions -> $unusedRegions")

                                            lifecycleScope.launch {

                                                deleteRegionsImpl(
                                                    unusedRegions,
                                                    regionsToBeDeletedBackup
                                                )

                                            }

                                        } catch (exception: Exception) {
                                            SweetAlertDialog(
                                                requireContext(),
                                                SweetAlertDialog.ERROR_TYPE
                                            )
                                                .setTitleText(getString(R.string.operation_not_completed))
                                                .setContentText(exception.message)
                                                .show()

                                            exception.printStackTrace()
                                        }


                                    }
                                    1 -> {
                                        try {
                                            lifecycleScope.launch {
                                                membersDeletionImpl(membersToBeDeletedBackUp)
                                            }
                                        } catch (exception: Exception) {
                                            SweetAlertDialog(
                                                requireContext(),
                                                SweetAlertDialog.ERROR_TYPE
                                            )
                                                .setTitleText(getString(R.string.operation_not_completed))
                                                .setContentText(exception.message)
                                                .show()

                                            exception.printStackTrace()
                                        }

                                    }
                                    2 -> {
                                        try {
                                            lifecycleScope.launch {
                                                membersToBeDeletedBackUp.clear()
                                                membersToBeDeletedBackUp.addAll(members)

                                                val deletedMembersCountInDeleteAll =
                                                    membersViewModel.deleteAllMembers()

                                                regionsToBeDeletedBackup.clear()
                                                regionsToBeDeletedBackup.addAll(regions)

                                                val deletedRegionsCountInDeleteAll =
                                                    regionsViewModel.deleteAllRegions(
                                                        regionsToBeDeletedBackup
                                                    )

                                                if (deletedMembersCountInDeleteAll > 0 && deletedRegionsCountInDeleteAll > 0) {
                                                    Timber.log(
                                                        Log.INFO,
                                                        "User cleaned up all the data successfully."
                                                    )

                                                    val regionsText = resources.getQuantityString(
                                                        R.plurals.regions_prefix,
                                                        deletedRegionsCountInDeleteAll,
                                                        deletedRegionsCountInDeleteAll
                                                    )
                                                    val membersText = resources.getQuantityString(
                                                        R.plurals.members_prefix,
                                                        deletedMembersCountInDeleteAll,
                                                        deletedMembersCountInDeleteAll
                                                    )
                                                    withContext(Dispatchers.Main) {

                                                        val snack = Snackbar.make(
                                                            this@SettingsFragment.requireView(),
                                                            getString(
                                                                R.string.delete_all_snack_message,
                                                                regionsText,
                                                                membersText
                                                            ),
                                                            Snackbar.LENGTH_LONG
                                                        ).setAction(R.string.undo_deletion) {
                                                            lifecycleScope.launch {

                                                                regionsToBeDeletedBackup.forEach { region ->
                                                                    val id = async {
                                                                        regionsViewModel.createRegion(
                                                                            region
                                                                        )
                                                                    }
                                                                }

                                                                membersViewModel.createNewMember(
                                                                    membersToBeDeletedBackUp
                                                                )
                                                            }
                                                        }

                                                        snack.show()
                                                    }

                                                } else if (deletedMembersCountInDeleteAll > 0) {
                                                    val snack = Snackbar.make(
                                                        this@SettingsFragment.requireView(),
                                                        getMemberDeletionCountString(
                                                            deletedMembersCountInDeleteAll
                                                        ),
                                                        Snackbar.LENGTH_LONG
                                                    ).setAction(R.string.undo_deletion) {
                                                        lifecycleScope.launch {
                                                            membersViewModel.createNewMember(
                                                                membersToBeDeletedBackUp
                                                            )
                                                        }
                                                    }

                                                    snack.show()
                                                } else if (deletedRegionsCountInDeleteAll > 0) {
                                                    val snack = Snackbar.make(
                                                        this@SettingsFragment.requireView(),
                                                        getRegionDeletionCountString(
                                                            deletedRegionsCountInDeleteAll
                                                        ),
                                                        Snackbar.LENGTH_LONG
                                                    ).setAction(R.string.undo_deletion) {
                                                        lifecycleScope.launch {

                                                            regionsToBeDeletedBackup.forEach { region ->
                                                                regionsViewModel.createRegion(
                                                                    region
                                                                )
                                                            }

                                                        }
                                                    }

                                                    snack.show()
                                                } else {
                                                    ToastUtils.showLongToast(getString(R.string.none_was_deleted))
                                                }
                                            }
                                        } catch (exception: Exception) {
                                            SweetAlertDialog(
                                                requireContext(),
                                                SweetAlertDialog.ERROR_TYPE
                                            )
                                                .setTitleText(getString(R.string.operation_not_completed))
                                                .setContentText(exception.message)
                                                .show()

                                            Timber.log(
                                                Log.ERROR,
                                                "Exception in cleaning up data.",
                                                exception
                                            )
                                        }
                                    }
                                }

                            }.onCanceled {
                                ToastUtils.showShortToast(getString(R.string.operation_cancelled))

                            }
                            .show()
                    }
                    .setNeutralButton(getString(R.string.dialog_cancel)) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()



                true
            }
    }

    private suspend fun deleteRegionsImpl(
        unusedRegions: List<RegionEntity>,
        regionsToBeDeletedBackup: MutableList<RegionEntity>
    ) {
        val deletionCount =
            regionsViewModel.deleteAllRegions(unusedRegions)

        withContext(Dispatchers.Main) {
            val regionsDeletionSnackbar = Snackbar.make(
                this@SettingsFragment.requireView(),
                getRegionDeletionCountString(deletionCount),
                Snackbar.LENGTH_SHORT
            ).setAction(
                getString(
                    R.string.undo_deletion
                )
            ) {
                lifecycleScope.launch {
                    regionsToBeDeletedBackup.forEach { region ->
                        regionsViewModel.createRegion(region)
                    }
                }
            }

            if (deletionCount > 0) {
                regionsDeletionSnackbar.show()
            } else {
                ToastUtils.showLongToast(getString(R.string.none_was_deleted))
            }
        }
    }

    private fun getMemberDeletionCountString(count: Int): String {
        val membersText = resources.getQuantityString(
            R.plurals.members_prefix,
            count,
            count
        )

        return getString(
            R.string.deletion_suffix,
            membersText
        )
    }

    private fun getRegionDeletionCountString(count: Int): String {
        val regionsText = resources.getQuantityString(
            R.plurals.regions_prefix,
            count,
            count
        )

        return getString(
            R.string.deletion_suffix,
            regionsText
        )
    }

    private suspend fun membersDeletionImpl(membersBackup: List<MemberEntity>) {

        val deletedMembersCount =
            membersViewModel.deleteAllMembers()

        withContext(Dispatchers.Main) {
            val membersDeletedSnackBar = Snackbar.make(
                this@SettingsFragment.requireView(),
                getMemberDeletionCountString(deletedMembersCount),
                Snackbar.LENGTH_SHORT
            ).setAction(
                getString(
                    R.string.undo_deletion
                )
            ) {
                lifecycleScope.launch {
                    membersViewModel.createNewMember(
                        membersBackup
                    )
                }
            }

            if (deletedMembersCount > 0) {
                membersDeletedSnackBar.show()
            } else {
                ToastUtils.showLongToast(getString(R.string.none_was_deleted))
            }

        }
    }

    private fun retrieveVersionNumber() {
        val versionText = findPreference<Preference>(getString(R.string.version_number))

        versionText?.setSummary(R.string.version)
    }

    private fun exportImport() {
        val exportImport = findPreference<Preference>(getString(R.string.export_import))

        exportImport?.onPreferenceClickListener =
            Preference.OnPreferenceClickListener {
                val importExportSelectionSheet = ImportExportSheet()

                importExportSelectionSheet.show(childFragmentManager, ImportExportSheet.TAG)

                true
            }
    }


    private fun joinSupport() {
        val supportPrompt = findPreference<Preference>(getString(R.string.join_support))

        supportPrompt?.onPreferenceClickListener = Preference.OnPreferenceClickListener {

            try {
                val browserIntent =
                    Intent(Intent.ACTION_VIEW, Uri.parse(TELEGRAM_SUPPORT_GROUP_LINK))
                Timber.log(Log.INFO, "User opened support group link.")
                startActivity(browserIntent)
            } catch (exception: Exception) {
                SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                    .setTitleText(getString(R.string.dialog_error_title))
                    .setContentText(
                        getString(
                            R.string.dialog_error_template, exception.message
                        )
                    )
                    .show()

                Timber.log(Log.ERROR, "Error attempting to join support group.", exception)
            }
            true
        }
    }

    companion object {
        const val TAG = "MergePromptImports"
    }

}