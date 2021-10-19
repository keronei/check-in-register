package com.keronei.koregister.fragments

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.keronei.domain.entities.RegionEntity
import com.keronei.kiregister.R
import com.keronei.kiregister.databinding.CreateRegionFragmentBinding
import com.keronei.koregister.viewmodels.RegionViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateRegionFragment : DialogFragment() {

    companion object {
        fun newInstance() = CreateRegionFragment()
    }

    val viewModel: RegionViewModel by activityViewModels()
    lateinit var createRegionFragmentBindings: CreateRegionFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        createRegionFragmentBindings = DataBindingUtil.inflate(
            inflater, R.layout.create_region_fragment, container, false
        )

        setupOnClickListeners()

        return createRegionFragmentBindings.root
    }

    private fun setupOnClickListeners() {
        createRegionFragmentBindings.createRegionButton.setOnClickListener {
            val providedName = createRegionFragmentBindings.regionNameEdittext.text.toString()

            if (providedName.isNotEmpty() && providedName.length > 2) {
                viewModel.createRegion(RegionEntity(0, providedName))
                this.dismiss()

                showToast("$providedName added.")
            } else {
                showToast("Name too short!")
            }
        }
    }

   private fun showToast(message: String) {
        val toast = Toast.makeText(context, message, Toast.LENGTH_LONG)
        toast.setGravity(Gravity.TOP, 0, 0)
        toast.show()
    }

    override fun onResume() {
        super.onResume()

        val params = dialog?.window?.attributes
        params?.width = MATCH_PARENT

        dialog?.window?.attributes = params
    }


}