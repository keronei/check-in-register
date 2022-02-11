package com.keronei.keroscheckin.fragments.importexport

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.keronei.keroscheckin.R
import com.keronei.keroscheckin.databinding.BottomModalMergeImportsBinding
import dagger.hilt.android.AndroidEntryPoint
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

@AndroidEntryPoint
class MergePromptImports : BottomSheetDialogFragment() {

    lateinit var importBindingOptions: BottomModalMergeImportsBinding

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

    companion object {
        const val TAG = "MergePromptImports"
    }
}