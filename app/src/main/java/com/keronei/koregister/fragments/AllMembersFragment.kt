package com.keronei.koregister.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.keronei.domain.entities.RegionEntity
import com.keronei.kiregister.R
import com.keronei.kiregister.databinding.AllMembersFragmentBinding
import com.keronei.kiregister.databinding.SelectedAttendeeOptionsBinding
import com.keronei.kiregister.databinding.SelectedRegonOptionsBinding
import com.keronei.koregister.adapter.AttendanceRecyclerAdapter
import com.keronei.koregister.models.AttendeePresentation
import com.keronei.koregister.models.toPresentation
import com.keronei.utils.ToastUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AllMembersFragment : Fragment() {

    private val allMembersViewModel: AllMembersViewModel by activityViewModels()
    lateinit var allMembersAdapter: AttendanceRecyclerAdapter
    lateinit var allMembersFragmentBinding: AllMembersFragmentBinding
    lateinit var selectedAttendeeOptions : SelectedAttendeeOptionsBinding

    companion object {
        fun newInstance() = AllMembersFragment()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        allMembersFragmentBinding =
            DataBindingUtil.inflate(inflater, R.layout.all_members_fragment, container, false)

        return allMembersFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupList()

        watchStatuses()

    }

    private fun setupList() {
        allMembersAdapter = AttendanceRecyclerAdapter(::selectedAttendee, requireContext())
        allMembersFragmentBinding.recyclerAllMembers.adapter = allMembersAdapter
    }

    private fun selectedAttendee(member : AttendeePresentation){
        selectedAttendeeOptions = SelectedAttendeeOptionsBinding.inflate(layoutInflater)

        selectedAttendeeOptions.selectedRegionName.text = member.name

        val optionsPrompt =
            MaterialAlertDialogBuilder(requireContext()).setView(selectedAttendeeOptions.root).show()

        val params = optionsPrompt?.window?.attributes
        params?.width = ViewGroup.LayoutParams.MATCH_PARENT

        optionsPrompt?.window?.attributes = params

        selectedAttendeeOptions.editRegion.setOnClickListener {
            val navigateToEdit = MembersFragmentDirections.actionMembersFragmentToCreateMemberFragment(true, member)

            findNavController().navigate(navigateToEdit)

            optionsPrompt.dismiss()
        }

        selectedAttendeeOptions.deleteRegion.setOnClickListener {


            optionsPrompt.dismiss()

        }

    }

    private fun watchStatuses() {
        lifecycleScope.launch {
            allMembersViewModel.queryAllMembersAttendance().collect { membersAttendance ->
                if (membersAttendance.isEmpty()) {
                    allMembersFragmentBinding.noRegisteredMemberTextView.visibility = View.VISIBLE
                    allMembersFragmentBinding.searchViewAllMembers.visibility = View.GONE
                } else {
                    allMembersFragmentBinding.noRegisteredMemberTextView.visibility = View.GONE
                    allMembersFragmentBinding.searchViewAllMembers.visibility = View.VISIBLE


                    allMembersAdapter.submitList(membersAttendance.map { entry -> entry.toPresentation() })
                }
            }
        }
    }


}