package com.keronei.koregister.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.keronei.domain.entities.RegionEntity
import com.keronei.kiregister.R
import com.keronei.kiregister.databinding.RegionFragmentBinding
import com.keronei.kiregister.databinding.SelectedRegonOptionsBinding
import com.keronei.koregister.adapter.RegionsRecyclerAdapter
import com.keronei.koregister.models.RegionPresentation
import com.keronei.koregister.models.toPresentation
import com.keronei.koregister.viewmodels.RegionViewModel
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

        val optionsPrompt =
            MaterialAlertDialogBuilder(requireContext()).setView(selectedRegionOptions.root).show()

        val params = optionsPrompt?.window?.attributes
        params?.width = ViewGroup.LayoutParams.MATCH_PARENT

        optionsPrompt?.window?.attributes = params

        selectedRegionOptions.editRegion.setOnClickListener {
            val navigateToEdit =
                RegionFragmentDirections.actionRegionsFragmentToCreateRegionFragment(true, region)

            findNavController().navigate(navigateToEdit)

            optionsPrompt.dismiss()
        }

        selectedRegionOptions.deleteRegion.setOnClickListener {
            if (region.memberCount.toInt() > 0) {
                ToastUtils.showLongToastInMiddle(R.string.still_has_members)
            } else {
                viewModel.deleteRegion(RegionEntity(region.id.toInt(), region.name))
                ToastUtils.showLongToastInMiddle(R.string.region_deleted)
            }

            optionsPrompt.dismiss()

        }

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