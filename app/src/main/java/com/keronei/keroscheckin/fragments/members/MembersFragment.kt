package com.keronei.keroscheckin.fragments.members

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.keronei.keroscheckin.R
import com.keronei.keroscheckin.adapter.AttendanceTabsAdapter
import com.keronei.keroscheckin.databinding.MembersFragmentBinding
import com.keronei.keroscheckin.viewmodels.AllMembersViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class MembersFragment : Fragment() {

    private lateinit var mTabs: TabLayout
    private lateinit var mViewPager: ViewPager2
    private lateinit var membersContentBinding: MembersFragmentBinding
    private val allMembersViewModel: AllMembersViewModel by viewModels()


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


        val adapter = AttendanceTabsAdapter(this)

        adapter.addFragment(AllMembersFragment(), "All")
        adapter.addFragment(YetToCheckInFragment(), "Yet to check in")


        mViewPager.adapter = adapter

        TabLayoutMediator(mTabs, mViewPager) { tab, position ->
            mViewPager.setCurrentItem(tab.position, true)
            tab.text = adapter.getPageTitle(position)
        }.attach()

        handleFabBehaviourBasedOnScrolls()

        return membersContentBinding.root
    }

    private fun handleFabBehaviourBasedOnScrolls() {
        lifecycleScope.launchWhenResumed {
            allMembersViewModel.membersFabVisibilityStatus.collect { currentStatus ->
                withContext(Dispatchers.Main) {
                    membersContentBinding.addMemberFabText.visibility =
                        if (currentStatus) View.VISIBLE else View.GONE
                }
            }
        }
    }

    private fun onClickListeners() {
        membersContentBinding.fabFullBtnMembers.setOnClickListener {
            val openCreateMemberAction =
                MembersFragmentDirections.actionMembersFragmentToCreateMemberFragment()

            findNavController().navigate(R.id.action_membersFragment_to_createMemberFragment)
        }

        membersContentBinding.settingsIcon.setOnClickListener {
            val settingsAction = MembersFragmentDirections.actionMembersFragmentToSettingsFragment()

            findNavController().navigate(settingsAction)
        }
    }

}