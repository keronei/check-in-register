package com.keronei.koregister.fragments.members

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.keronei.kiregister.R
import com.keronei.kiregister.databinding.MembersFragmentBinding
import com.keronei.koregister.adapter.AttendanceTabsAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MembersFragment : Fragment() {

    private lateinit var mTabs: TabLayout
    private lateinit var mViewPager: ViewPager
    lateinit var membersContentBinding: MembersFragmentBinding

    companion object {
        fun newInstance() = MembersFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        membersContentBinding =
            DataBindingUtil.inflate(inflater, R.layout.members_fragment, container, false)

        mTabs = membersContentBinding.tabs
        mViewPager = membersContentBinding.viewpager

        onClickListeners()

        val adapter = AttendanceTabsAdapter(childFragmentManager)

        adapter.addFragment(AllMembersFragment(), "All")
        adapter.addFragment(YetToCheckedInFragment(), "Yet to check in")

        mTabs.setupWithViewPager(mViewPager)

        mViewPager.adapter = adapter

        return membersContentBinding.root
    }

    private fun onClickListeners() {
        membersContentBinding.createNewMember.setOnClickListener {
            val openCreateMemberAction =
                MembersFragmentDirections.actionMembersFragmentToCreateMemberFragment()

            findNavController().navigate(openCreateMemberAction)
        }
    }

}