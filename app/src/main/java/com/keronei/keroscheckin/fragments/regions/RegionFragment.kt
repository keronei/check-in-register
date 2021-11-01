package com.keronei.keroscheckin.fragments.regions

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.keronei.domain.entities.RegionEntity
import com.keronei.keroscheckin.R
import com.keronei.keroscheckin.databinding.RegionFragmentBinding
import com.keronei.keroscheckin.databinding.SelectedRegonOptionsBinding
import com.keronei.keroscheckin.adapter.RegionsRecyclerAdapter
import com.keronei.keroscheckin.models.RegionPresentation
import com.keronei.keroscheckin.models.toPresentation
import com.keronei.keroscheckin.viewmodels.RegionViewModel
import com.keronei.utils.ToastUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegionFragment : Fragment() {

    companion object {
        fun newInstance() = RegionFragment()
    }

    private val viewModel: RegionViewModel by activityViewModels()
    lateinit var regionsAdapter: RegionsRecyclerAdapter
    lateinit var regionFragmentBinding: RegionFragmentBinding
    lateinit var selectedRegionOptions: SelectedRegonOptionsBinding
    private var navController: NavController? = null
    private lateinit var searchView: androidx.appcompat.widget.SearchView
    private var optionsPrompt: androidx.appcompat.app.AlertDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        regionFragmentBinding =
            DataBindingUtil.inflate(inflater, R.layout.region_fragment, container, false)

        searchView = regionFragmentBinding.searchViewRegions



        setupOnClickListeners()

        navController = childFragmentManager.findFragmentById(R.id.nav_host_fragment)
            ?.findNavController()

        return regionFragmentBinding.root
    }

    private fun setupOnClickListeners() {
        regionFragmentBinding.createNewEntryFab.setOnClickListener {

            findNavController().navigate(R.id.action_regionsFragment_to_createRegionFragment)
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                regionsAdapter.filter(newText)
                return true
            }

        })
    }

    private fun setupRegionsList() {
        regionsAdapter = RegionsRecyclerAdapter(::itemSelected, requireContext())
        regionFragmentBinding.regionsRecycler.adapter = regionsAdapter
    }

    private fun itemSelected(region: RegionPresentation) {

        selectedRegionOptions = SelectedRegonOptionsBinding.inflate(layoutInflater)

        selectedRegionOptions.selectedRegionName.text = region.name

        optionsPrompt?.dismiss()

        optionsPrompt =
            MaterialAlertDialogBuilder(requireContext()).setView(selectedRegionOptions.root).show()

        val params = optionsPrompt?.window?.attributes
        params?.width = ViewGroup.LayoutParams.MATCH_PARENT

        optionsPrompt?.window?.attributes = params

        selectedRegionOptions.editRegion.setOnClickListener {
            val navigateToEdit =
                RegionFragmentDirections.actionRegionsFragmentToCreateRegionFragment(true, region)

            findNavController().navigate(navigateToEdit)

            optionsPrompt?.dismiss()
        }

        selectedRegionOptions.deleteRegion.setOnClickListener {
            if (region.memberCount.toInt() > 0) {
                ToastUtils.showLongToastInMiddle(R.string.still_has_members)
            } else {


                promptRegionDeletion(RegionEntity(region.id.toInt(), region.name))
            }

            optionsPrompt?.dismiss()

        }

    }

    private fun promptRegionDeletion(entry: RegionEntity) {
        SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
            .setTitleText("Delete ${entry.name}")
            .setContentText(
                "${entry.name} does not have any member registered. Are you sure you want to delete ?"
            )
            .setConfirmText("Yes")
            .setConfirmClickListener { sDialog ->
                viewModel.deleteRegion(entry)
                ToastUtils.showLongToastInMiddle(R.string.region_deleted)

                sDialog.dismissWithAnimation()
            }
            .setCancelButton(
                "Cancel"
            ) { sDialog -> sDialog.dismissWithAnimation() }
            .show()
    }

    private fun watchRegions() {
        lifecycleScope.launch {

            viewModel.queryAllRegionsWithMembersData().collect { info ->

                Log.d("RegionFragment", "Collected $info")

                val list = info.map { regionEmbedEntity -> regionEmbedEntity.toPresentation() }

                regionsAdapter.modifyList(list)
            }

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRegionsList()

        watchRegions()
    }

}