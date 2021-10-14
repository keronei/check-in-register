package com.keronei.koregister.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.keronei.kiregister.R
import com.keronei.kiregister.databinding.RegionFragmentBinding
import com.keronei.koregister.adapter.RegionsRecyclerAdapter
import com.keronei.koregister.viewmodels.RegionViewModel
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        regionFragmentBinding =
            DataBindingUtil.inflate(inflater, R.layout.region_fragment, container, false)
        return regionFragmentBinding.root
    }

    private fun setupRegionsList() {
        regionsAdapter = RegionsRecyclerAdapter()
        regionFragmentBinding.regionsRecycler.adapter = regionsAdapter
    }

    private fun watchRegions() {
        lifecycleScope.launch {

            viewModel.queryAllRegionsWithMembersData()

            viewModel.regionsInformation.collect { freshList ->
                regionsAdapter.submitList(freshList)

            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRegionsList()

        watchRegions()
    }

}