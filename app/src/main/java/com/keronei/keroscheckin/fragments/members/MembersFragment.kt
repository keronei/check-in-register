package com.keronei.keroscheckin.fragments.members

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.keronei.keroscheckin.R
import com.keronei.keroscheckin.adapter.AttendanceTabsAdapter
import com.keronei.keroscheckin.databinding.MembersFragmentBinding
import com.keronei.keroscheckin.viewmodels.AllMembersViewModel
import com.keronei.keroscheckin.viewmodels.ImportExportViewModel
import com.keronei.utils.ToastUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import timber.log.Timber

@AndroidEntryPoint
class MembersFragment : Fragment() {

    private lateinit var mTabs: TabLayout
    private lateinit var mViewPager: ViewPager2
    private lateinit var membersContentBinding: MembersFragmentBinding
    private val allMembersViewModel: AllMembersViewModel by activityViewModels()
    private val importExportViewModel: ImportExportViewModel by activityViewModels()


    companion object {
        const val TAG = "MemberFragment"
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

        adapter.addFragment(AllMembersFragment(), getString(R.string.all_members_tab_header))
        adapter.addFragment(YetToCheckInFragment(), getString(R.string.yet_to_check_in_header))


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
            allMembersViewModel.membersFabVisibilityStatusPropagate.collect { flipStatus ->
                membersContentBinding.addMemberFabText.visibility =
                    if (flipStatus) View.VISIBLE else View.GONE
            }
        }
    }

    private fun onClickListeners() {
        membersContentBinding.fabFullBtnMembers.setOnClickListener {
            navigateToCreateNew()
        }

        membersContentBinding.createNewMember.setOnClickListener {
            navigateToCreateNew()

        }

        membersContentBinding.settingsIcon.setOnClickListener {
            val settingsAction = MembersFragmentDirections.actionMembersFragmentToSettingsFragment()
            Timber.log(Log.INFO, "User opened settings.")

            findNavController().navigate(settingsAction)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (importExportViewModel.launchedIntentInputStream.value != null) {
            //navigate to settings, then pick import and start process.
            val autoLaunch = MembersFragmentDirections.actionMembersFragmentToSettingsFragment()
            findNavController().navigate(autoLaunch)
        }

    }

    private fun navigateToCreateNew() {
        findNavController().navigate(R.id.action_membersFragment_to_createMemberFragment)
    }

}