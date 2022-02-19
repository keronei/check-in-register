package com.keronei.keroscheckin.fragments.regions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.keronei.domain.entities.RegionEntity
import com.keronei.keroscheckin.R
import com.keronei.keroscheckin.databinding.CreateRegionFragmentBinding
import com.keronei.keroscheckin.models.RegionPresentation
import com.keronei.keroscheckin.models.constants.GUEST_ENTRY
import com.keronei.keroscheckin.viewmodels.RegionViewModel
import com.keronei.utils.ToastUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateRegionFragment : Fragment() {

    private val args: CreateRegionFragmentArgs by navArgs()

    var isEditing = true

    var selectedRegion: RegionPresentation? = null

    companion object {
        fun newInstance() = CreateRegionFragment()
    }

    val viewModel: RegionViewModel by activityViewModels()
    lateinit var createRegionFragmentBindings: CreateRegionFragmentBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isEditing = args.editing

        selectedRegion = args.selectedRegion

        if (isEditing) {
            createRegionFragmentBindings.createRegionButton.text = getString(R.string.action_update)

            createRegionFragmentBindings.regionNameEdittext.setText(selectedRegion?.name)

            if (selectedRegion?.name == GUEST_ENTRY) {
                createRegionFragmentBindings.regionNameEdittext.isEnabled = false
                createRegionFragmentBindings.createRegionButton.isEnabled = false
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        createRegionFragmentBindings = DataBindingUtil.inflate(
            inflater, R.layout.create_region_fragment, container, false
        )

        setupOnClickListeners()

        configureToolBar()

        return createRegionFragmentBindings.root
    }

    private fun setupOnClickListeners() {
        createRegionFragmentBindings.createRegionButton.setOnClickListener {
            val providedName = createRegionFragmentBindings.regionNameEdittext.text.toString()

            if (providedName.trim().isNotEmpty() && providedName.trim().length > 2) {
                if (isEditing) {

                    if (selectedRegion == null) return@setOnClickListener

                    viewModel.updateRegion(RegionEntity(selectedRegion!!.id.toInt(), providedName))
                    ToastUtils.showLongToastOnTop(R.string.entry_updated)
                } else {
                    viewModel.createRegion(listOf(RegionEntity(0, providedName.trim())))
                    ToastUtils.showLongToastOnTop(R.string.entry_added)
                }
                findNavController().popBackStack()


            } else {
                ToastUtils.showLongToastInMiddle(R.string.name_too_short)
            }
        }
    }

    private fun configureToolBar() {
        with(createRegionFragmentBindings.createRegionToolbar) {
            setNavigationIcon(R.drawable.ic_navigate_back_24)
            setNavigationOnClickListener {
                findNavController().popBackStack()
            }
        }
    }


}