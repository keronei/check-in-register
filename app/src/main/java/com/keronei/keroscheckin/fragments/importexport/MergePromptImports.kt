package com.keronei.keroscheckin.fragments.importexport

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.keronei.keroscheckin.R
import com.keronei.keroscheckin.databinding.BottomModalMergeImportsBinding
import com.keronei.keroscheckin.viewmodels.ImportExportViewModel
import com.keronei.keroscheckin.viewmodels.MemberViewModel
import com.keronei.keroscheckin.viewmodels.RegionViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle
import javax.inject.Inject

@AndroidEntryPoint
class MergePromptImports : Fragment() {

    lateinit var importBindingOptions: BottomModalMergeImportsBinding

    private val importExportViewModel : ImportExportViewModel by activityViewModels()

    private val regionsViewModel: RegionViewModel by activityViewModels()

    private val memberViewModel: MemberViewModel by activityViewModels()

    @Inject
    lateinit var coroutineScope: CoroutineScope

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        importBindingOptions =
            DataBindingUtil.inflate(inflater, R.layout.bottom_modal_merge_imports, container, false)

        setOnclickListeners()

        return importBindingOptions.root
    }


    private fun setOnclickListeners() {
        importBindingOptions.replace.setOnClickListener {

            cleanUpLocalData()

            insertNewImports()

            MotionToast.createToast(
                requireActivity(),
                "Success",
                "Import Completed successfully!",
                MotionToastStyle.SUCCESS,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.LONG_DURATION,
                ResourcesCompat.getFont(requireActivity(), R.font.helvetica_regular)
            )

        }

        importBindingOptions.merge.setOnClickListener {
            MotionToast.createToast(
                requireActivity(),
                "Success",
                "All entries saved.",
                MotionToastStyle.SUCCESS,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.LONG_DURATION,
                ResourcesCompat.getFont(requireActivity(), R.font.helvetica_regular)
            )

        }
    }

    private fun insertNewImports() {
        coroutineScope.launch {
            regionsViewModel.createRegion(importExportViewModel.parsedRegionsToImport.value)

            //only start feeding members when regions are done for dependency
            memberViewModel.createNewMember(importExportViewModel.parsedMembersToImport.value)
        }
    }

    private fun cleanUpLocalData(){
        coroutineScope.launch {

            //just re-query and delete all
            val existingRegions = regionsViewModel.queryAllRegions().first()

            memberViewModel.deleteAllMembers()

            regionsViewModel.deleteAllRegions(existingRegions)

        }
    }

    companion object {
        const val TAG = "MergePromptImports"
    }
}