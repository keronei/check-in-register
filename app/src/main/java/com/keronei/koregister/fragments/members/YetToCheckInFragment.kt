package com.keronei.koregister.fragments.members

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.keronei.kiregister.R
import com.keronei.kiregister.databinding.YetToCheckedInFragmentBinding
import com.keronei.koregister.adapter.AttendanceRecyclerAdapter
import com.keronei.koregister.models.AttendeePresentation
import com.keronei.koregister.models.toPresentation
import com.keronei.koregister.viewmodels.AllMembersViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class YetToCheckInFragment : Fragment() {

    companion object {
        fun newInstance() = YetToCheckInFragment()
    }

    private val allMembersViewModel: AllMembersViewModel by viewModels()
    private lateinit var yetToCheckInBinding: YetToCheckedInFragmentBinding
    lateinit var yetToCheckInAdapter: AttendanceRecyclerAdapter
    lateinit var searchView: androidx.appcompat.widget.SearchView
    private var invalidationPeriod = 8

    @Inject
    lateinit var preferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        yetToCheckInBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.yet_to_checked_in_fragment,
            container,
            false
        )

        searchView = yetToCheckInBinding.searchViewYetToCheckIn

        invalidationPeriod =
            preferences.getString(getString(R.string.invalidate_period_key), "8")?.toInt() ?: 8

        return yetToCheckInBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupList()
        watchStatuses()
        setUpOnClickListeners()
    }

    private fun setUpOnClickListeners() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                yetToCheckInAdapter.filter(newText)
                return true
            }

        })
    }

    private fun watchStatuses() {
        lifecycleScope.launch {
            allMembersViewModel.queryAllMembersAttendance().collect { membersAttendance ->
                if (membersAttendance.isEmpty()) {
                    yetToCheckInBinding.allMembersCheckedInTextview.visibility = View.VISIBLE
                    yetToCheckInBinding.searchViewYetToCheckIn.visibility = View.GONE
                } else {
                    yetToCheckInBinding.allMembersCheckedInTextview.visibility = View.GONE
                    yetToCheckInBinding.searchViewYetToCheckIn.visibility = View.VISIBLE
                    membersAttendance.map { entry ->

                        entry.toPresentation(invalidationPeriod).takeIf { member ->
                            member.lastCheckInStamp == null
                        }
                    }
                        .let { finalList ->

                            val temp = mutableListOf<AttendeePresentation>()

                            finalList.forEach { nullableItem ->

                                if (nullableItem != null) {
                                    temp.add(nullableItem)
                                }

                            }
                            yetToCheckInAdapter.modifyList(temp)

                        }
                }
            }
        }


    }

    private fun setupList() {
        yetToCheckInAdapter = AttendanceRecyclerAdapter(::actOnSelection)
        yetToCheckInBinding.recyclerYetToCheckIn.adapter = yetToCheckInAdapter
    }

    private fun actOnSelection(attendeePresentation: AttendeePresentation) {

    }
}