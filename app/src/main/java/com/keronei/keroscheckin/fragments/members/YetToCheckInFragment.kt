package com.keronei.keroscheckin.fragments.members

import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.keronei.keroscheckin.R
import com.keronei.keroscheckin.adapter.AttendanceRecyclerAdapter
import com.keronei.keroscheckin.databinding.FragmentYetToCheckedInBinding
import com.keronei.keroscheckin.models.AttendeePresentation
import com.keronei.keroscheckin.models.constants.CHECK_IN_INVALIDATE_DEFAULT_PERIOD
import com.keronei.keroscheckin.models.toPresentation
import com.keronei.keroscheckin.viewmodels.AllMembersViewModel
import com.keronei.utils.ToastUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class YetToCheckInFragment : Fragment() {

    companion object {
        fun newInstance() = YetToCheckInFragment()
        const val TAG = "YetToCheckInFragment"
    }

    private val allMembersViewModel: AllMembersViewModel by activityViewModels()
    private lateinit var yetToCheckInBinding: FragmentYetToCheckedInBinding
    lateinit var yetToCheckInAdapter: AttendanceRecyclerAdapter
    lateinit var searchView: androidx.appcompat.widget.SearchView
    private var invalidationPeriod = CHECK_IN_INVALIDATE_DEFAULT_PERIOD.toInt()

    @Inject
    lateinit var preferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        yetToCheckInBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_yet_to_checked_in,
            container,
            false
        )

        searchView = yetToCheckInBinding.searchViewYetToCheckIn

        invalidationPeriod =
            preferences.getString(
                getString(R.string.invalidate_period_key),
                CHECK_IN_INVALIDATE_DEFAULT_PERIOD
            )?.toInt() ?: CHECK_IN_INVALIDATE_DEFAULT_PERIOD.toInt()

        return yetToCheckInBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupList()
        watchStatuses()
        setUpOnClickListeners()
        listenToFab()
    }

    private fun listenToFab() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            yetToCheckInBinding.nestedScrollViewYetToCheckIn.setOnScrollChangeListener { _, scrollX, scrollY, _, oldScrollY ->

                when {
                    scrollY > oldScrollY -> {
                        flipFab(false)
                    }
                    scrollX == scrollY -> {
                        flipFab(true)
                    }
                    else -> {
                        flipFab(true)
                    }

                }
            }

        }
    }

    private fun flipFab(status: Boolean) {
        allMembersViewModel.membersFabVisibilityStatus.value = status
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

        val currentTime = Calendar.getInstance()

        val hourToSet = currentTime.get(Calendar.HOUR_OF_DAY)

        val finalHour = hourToSet - invalidationPeriod

        currentTime.set(Calendar.HOUR_OF_DAY, finalHour)

        lifecycleScope.launch {
            allMembersViewModel.allMembersData.collect { membersAttendance ->
                if (membersAttendance.isEmpty()) {
                    yetToCheckInBinding.allMembersCheckedInTextview.visibility = View.VISIBLE
                    yetToCheckInBinding.searchViewYetToCheckIn.visibility = View.GONE
                } else {
                    yetToCheckInBinding.allMembersCheckedInTextview.visibility = View.GONE
                    yetToCheckInBinding.searchViewYetToCheckIn.visibility = View.VISIBLE
                    val presentationList = membersAttendance.map { entry ->
                        entry.toPresentation(invalidationPeriod)

                    }

                    val finalList = presentationList.filter { entry ->
                        entry.lastCheckInStamp == null || entry.lastCheckInStamp < currentTime.timeInMillis
                    }

                    val inactiveShouldBeHidden =
                        preferences.getBoolean(
                            getString(R.string.inactive_members_pref_key),
                            false
                        )

                    val filteredList =
                        if (inactiveShouldBeHidden) finalList.filter { memberEntry -> memberEntry.isActive } else finalList

                    val checkedInButMarkedInactive =
                        presentationList.filter { member ->
                            member.lastCheckInStamp != null &&
                                    member.lastCheckInStamp > currentTime.timeInMillis &&
                                    !member.isActive &&
                                    inactiveShouldBeHidden
                        }

                    yetToCheckInAdapter.modifyList(filteredList)

                    val memberText = resources.getQuantityString(
                        R.plurals.members_prefix,
                        filteredList.size,
                        filteredList.size
                    )


                    val filterHint = getString(R.string.filter_hint, memberText)

                    yetToCheckInBinding.searchViewYetToCheckIn.queryHint = filterHint

                    if (filteredList.isEmpty()) {
                        yetToCheckInBinding.allMembersCheckedInTextview.visibility =
                            View.VISIBLE
                        yetToCheckInBinding.searchViewYetToCheckIn.visibility = View.GONE
                        if (finalList.isEmpty() && !inactiveShouldBeHidden && presentationList.isNotEmpty()) {
                            yetToCheckInBinding.allMembersCheckedInTextview.text =
                                getString(R.string.all_checked_in)
                        } else if (checkedInButMarkedInactive.isNotEmpty()) {
                            val membersText = resources.getQuantityString(
                                R.plurals.members_prefix,
                                checkedInButMarkedInactive.size,
                                checkedInButMarkedInactive.size
                            )
                            yetToCheckInBinding.allMembersCheckedInTextview.text =
                                getString(R.string.checked_in_but_marked_inactive, membersText)

                        } else if (presentationList.isNotEmpty() && filteredList.isEmpty() && inactiveShouldBeHidden) {
                            val diff = finalList - filteredList

                            val membersText = resources.getQuantityString(
                                R.plurals.members_prefix,
                                diff.size,
                                diff.size
                            )

                            yetToCheckInBinding.allMembersCheckedInTextview.text =
                                getString(R.string.all_active_checked_in, membersText)
                        } else {
                            //No member
                            yetToCheckInBinding.allMembersCheckedInTextview.text =
                                getString(R.string.no_member_attendance_data)
                        }
                    } else {
                        yetToCheckInBinding.allMembersCheckedInTextview.visibility =
                            View.GONE
                        yetToCheckInBinding.searchViewYetToCheckIn.visibility = View.VISIBLE
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
        if (attendeePresentation.lastCheckInStamp != null) {
            val lastSeen = DateUtils.getRelativeTimeSpanString(
                attendeePresentation.lastCheckInStamp
            )

            ToastUtils.showLongToast(
                getString(
                    R.string.member_last_seen_detail,
                    attendeePresentation.firstName,
                    lastSeen
                )
            )
        } else {
            ToastUtils.showLongToast(
                getString(
                    R.string.member_never_checked_in,
                    attendeePresentation.firstName
                )
            )
        }
    }
}