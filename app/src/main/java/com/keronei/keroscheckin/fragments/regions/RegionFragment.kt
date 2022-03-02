package com.keronei.keroscheckin.fragments.regions

import android.os.Build
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
import com.google.android.material.snackbar.Snackbar
import com.keronei.data.remote.Constants
import com.keronei.domain.entities.RegionEntity
import com.keronei.keroscheckin.R
import com.keronei.keroscheckin.databinding.RegionFragmentBinding
import com.keronei.keroscheckin.databinding.SelectedRegonOptionsBinding
import com.keronei.keroscheckin.adapter.RegionsRecyclerAdapter
import com.keronei.keroscheckin.models.RegionPresentation
import com.keronei.keroscheckin.models.constants.GUEST_ENTRY
import com.keronei.keroscheckin.models.toPresentation
import com.keronei.keroscheckin.viewmodels.RegionViewModel
import com.keronei.utils.ToastUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject

@AndroidEntryPoint
class RegionFragment : Fragment() {

    companion object {
        fun newInstance() = RegionFragment()
        const val TAG = "RegionFragment"
    }

    @Inject
    lateinit var coroutineScope: CoroutineScope

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

        watchFab()

        setupOnClickListeners()

        navController = childFragmentManager.findFragmentById(R.id.nav_host_fragment)
            ?.findNavController()

        return regionFragmentBinding.root
    }

    private fun watchFab() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            regionFragmentBinding.regionsRecycler.setOnScrollChangeListener { _, scrollX, scrollY, _, oldScrollY ->
                with(regionFragmentBinding.addRegionFabText) {
                    visibility = when {
                        scrollY > oldScrollY -> {
                            View.GONE
                        }
                        scrollX == scrollY -> {
                            View.VISIBLE
                        }
                        else -> {
                            View.VISIBLE
                        }
                    }
                }
            }
        }
    }

    private fun setupOnClickListeners() {
        regionFragmentBinding.fabFullBtnRegions.setOnClickListener {
            navigateToCreateNew()
        }

        regionFragmentBinding.createNewMember.setOnClickListener {
            navigateToCreateNew()
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

    private fun navigateToCreateNew() {
        findNavController().navigate(R.id.action_regionsFragment_to_createRegionFragment)

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

        if (region.name == GUEST_ENTRY) {
            selectedRegionOptions.deleteRegion.isEnabled = false
        }

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
        val regionBackUp = mutableListOf<RegionEntity>()

        regionBackUp.add(entry)

        SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
            .setTitleText("Delete ${entry.name}")
            .setContentText(
                "${entry.name} does not have any member registered. Are you sure you want to delete ?"
            )
            .setConfirmText("Yes")
            .setConfirmClickListener { sDialog ->
                coroutineScope.launch {
                    try {
                        val deletionCount = viewModel.deleteRegion(entry)

                        if (deletionCount > 0) {
                            val snack = Snackbar.make(
                                regionFragmentBinding.root,
                                getString(R.string.region_deleted),
                                Snackbar.LENGTH_LONG
                            ).setAction(R.string.undo_deletion) {
                                coroutineScope.launch {
                                    regionBackUp.forEach { backedUpRegion ->
                                        async {
                                            viewModel.createRegion(backedUpRegion)
                                        }
                                    }

                                }
                            }

                            snack.show()

                        } else {
                            ToastUtils.showShortToast(R.string.operation_not_completed)

                        }
                    } catch (exception: Exception) {
                        Timber.log(Log.ERROR, "Failed to delete region.", exception)
                        ToastUtils.showShortToast(R.string.operation_not_completed)
                    }

                }

                sDialog.dismissWithAnimation()
            }
            .setCancelButton(
                getString(R.string.dialog_cancel)
            ) { sDialog -> sDialog.dismissWithAnimation() }
            .show()
    }

    private fun watchRegions() {
        lifecycleScope.launch {

            viewModel.queryAllRegionsWithMembersData().collect { info ->

                val list = info.map { regionEmbedEntity -> regionEmbedEntity.toPresentation() }

                regionsAdapter.modifyList(list)

                if (list.isEmpty()) {
                    regionFragmentBinding.emptyStateMessageRegion.visibility = View.VISIBLE
                    regionFragmentBinding.searchViewRegions.visibility = View.GONE
                } else {
                    regionFragmentBinding.emptyStateMessageRegion.visibility = View.GONE
                    regionFragmentBinding.searchViewRegions.visibility = View.VISIBLE
                    val regionsText =
                        resources.getQuantityString(R.plurals.regions_prefix, list.size, list.size)
                    regionFragmentBinding.searchViewRegions.queryHint =
                        getString(R.string.filter_regions_hint, regionsText)


                }
            }

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRegionsList()

        watchRegions()
    }

}