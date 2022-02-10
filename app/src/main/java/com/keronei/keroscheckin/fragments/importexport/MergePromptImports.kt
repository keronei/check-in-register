package com.keronei.keroscheckin.fragments.importexport

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.keronei.keroscheckin.R
import com.keronei.keroscheckin.databinding.BottomModalMergeImportsBinding
import dagger.hilt.android.AndroidEntryPoint

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

        return importBindingOptions.root
    }

    companion object {
        const val TAG = "MergePromptImports"
    }
}