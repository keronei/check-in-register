package com.keronei.koregister.fragments.members

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.keronei.kiregister.R
import com.keronei.kiregister.databinding.MembersFragmentBinding
import com.keronei.koregister.adapter.AttendanceTabsAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.members_fragment.*

@AndroidEntryPoint
class MembersFragment : Fragment() {

    private lateinit var mTabs: TabLayout
    private lateinit var mViewPager: ViewPager2
    private lateinit var membersContentBinding: MembersFragmentBinding

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

        return membersContentBinding.root
    }


//    override fun onBackPressed() {
//        if (viewpager.currentItem == 0) {
//            super.onBackPressed()
//        } else {
//            viewpager.currentItem = viewpager.currentItem - 1
//        }
//    }

    private fun onClickListeners() {
        membersContentBinding.createNewMember.setOnClickListener {
            val openCreateMemberAction =
                MembersFragmentDirections.actionMembersFragmentToCreateMemberFragment()

            findNavController().navigate(openCreateMemberAction)
        }

        membersContentBinding.settingsIcon.setOnClickListener {
            val settingsAction = MembersFragmentDirections.actionMembersFragmentToSettingsFragment()

            findNavController().navigate(settingsAction)
        }
    }

}