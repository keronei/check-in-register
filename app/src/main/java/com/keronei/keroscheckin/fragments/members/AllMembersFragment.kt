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
import androidx.navigation.fragment.findNavController
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.keronei.domain.entities.CheckInEntity
import com.keronei.keroscheckin.R
import com.keronei.keroscheckin.adapter.AttendanceRecyclerAdapter
import com.keronei.keroscheckin.databinding.DialogLayoutCheckInBinding
import com.keronei.keroscheckin.databinding.FragmentAllMembersBinding
import com.keronei.keroscheckin.databinding.SelectedAttendeeOptionsBinding
import com.keronei.keroscheckin.fragments.checkin.TimePickerFragment
import com.keronei.keroscheckin.models.AttendeePresentation
import com.keronei.keroscheckin.models.constants.CHECK_IN_INVALIDATE_DEFAULT_PERIOD
import com.keronei.keroscheckin.models.constants.TEMPERATURE_CEIL
import com.keronei.keroscheckin.models.constants.TEMPERATURE_FLOOR
import com.keronei.keroscheckin.models.toMemberEntity
import com.keronei.keroscheckin.models.toPresentation
import com.keronei.keroscheckin.viewmodels.AllMembersViewModel
import com.keronei.keroscheckin.viewmodels.CheckInViewModel
import com.keronei.keroscheckin.viewmodels.MemberViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class AllMembersFragment : Fragment() {

    private val allMembersViewModel: AllMembersViewModel by activityViewModels()
    private val checkInViewModel: CheckInViewModel by activityViewModels()
    private val memberViewModel: MemberViewModel by activityViewModels()
    lateinit var allMembersAdapter: AttendanceRecyclerAdapter
    lateinit var allMembersFragmentBinding: FragmentAllMembersBinding
    lateinit var selectedAttendeeOptions: SelectedAttendeeOptionsBinding
    lateinit var searchView: androidx.appcompat.widget.SearchView
    private var checkInViewBinding: DialogLayoutCheckInBinding? = null
    private val parser = SimpleDateFormat("hh:mm:ss a", Locale.US)
    private lateinit var checkInPrompt: androidx.appcompat.app.AlertDialog
    private var memberAtCheckIn: AttendeePresentation? = null
    private var invalidationPeriod = CHECK_IN_INVALIDATE_DEFAULT_PERIOD.toInt()
    private var optionsPrompt: androidx.appcompat.app.AlertDialog? = null

    private lateinit var checkInDialogSuccess: SweetAlertDialog

    @Inject
    lateinit var coroutineScope: CoroutineScope

    @Inject
    lateinit var preferences: SharedPreferences

    companion object {
        fun newInstance() = AllMembersFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        watchStatuses()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        allMembersFragmentBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_all_members, container, false)

        searchView = allMembersFragmentBinding.searchViewAllMembers

        val key = getString(R.string.invalidate_period_key)

        invalidationPeriod = preferences.getString(key, CHECK_IN_INVALIDATE_DEFAULT_PERIOD)?.toInt()
            ?: CHECK_IN_INVALIDATE_DEFAULT_PERIOD.toInt()

        return allMembersFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupList()


        setOnClickListeners()

        listenToFab()
    }

    private fun listenToFab() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            allMembersFragmentBinding.nestedScrollViewMembers.setOnScrollChangeListener { _, scrollX, scrollY, _, oldScrollY ->

                when {
                    scrollY > oldScrollY -> {
                        flipFab(false)
                    }
                    scrollX == scrollY -> {
                        flipFab(true)
                    }
                    else -> {
                        flipFab(false)

                    }
                }
            }

        }
    }

    private fun flipFab(state: Boolean) {
        allMembersViewModel.membersFabVisibilityStatus.value = state

    }

    private fun setOnClickListeners() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                allMembersAdapter.filter(newText)
                return true
            }

        })


    }

    private fun listenToDialogClicks() {
        checkInViewBinding?.textClockArrivalTime?.setOnClickListener {
            alternateTimeSelectionForArrivalTime()
        }

        checkInViewBinding?.textviewArrivalTimeCustom?.setOnClickListener {
            alternateTimeSelectionForArrivalTime()
        }

        checkInViewBinding?.confirmCheckinButton?.setOnClickListener {

            if (checkInViewBinding?.recordedTemperatureEdittext?.text.isNullOrEmpty()) {
                checkInViewBinding?.recordedTemperatureEdittext?.error =
                    getString(R.string.provide_measured_temp_prompt_text)
                return@setOnClickListener
            }

            val providedTemperature =
                checkInViewBinding?.recordedTemperatureEdittext?.text.toString()


            when {
                providedTemperature.toDouble() > TEMPERATURE_CEIL -> {
                    SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                        .setTitleText(getString(R.string.too_high_temp_warning_text))
                        .setContentText("$providedTemperature ºC is higher than accepted.")
                        .show()
                }
                providedTemperature.toDouble() < TEMPERATURE_FLOOR -> {
                    SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                        .setTitleText(getString(R.string.too_low_warning_dialog_text))
                        .setContentText("$providedTemperature ºC is lower than accepted.")
                        .show()
                }
                else -> {
                    //all is set
                    val isUsingDefaultTime = checkInViewBinding?.textClockArrivalTime?.visibility

                    val selectedTime =
                        if (isUsingDefaultTime == View.VISIBLE) Calendar.getInstance().time else checkInViewModel.customSelectedTime.value.time

                    val checkInInstance = CheckInEntity(
                        0,
                        memberAtCheckIn!!.memberId,
                        selectedTime.time,
                        providedTemperature.toDouble()
                    )

                    if (!memberAtCheckIn!!.isActive) {
                        checkInPrompt.dismiss()
                        attemptingToCheckInInactiveMember(memberAtCheckIn!!, checkInInstance)
                        return@setOnClickListener
                    }

                    checkInViewModel.checkInMember(
                        checkInInstance,
                        memberAtCheckIn!!.toMemberEntity()
                    )

                    checkInPrompt.dismiss()

                    showCheckedInSuccess()
                }
            }
        }
    }

    private fun showCheckedInSuccess() {
        checkInDialogSuccess = SweetAlertDialog(
            requireContext(), SweetAlertDialog.SUCCESS_TYPE
        )
            .setTitleText(getString(R.string.done_dialog_btn_text))
            .setContentText(
                getString(
                    R.string.member_checked_in_success_header,
                    memberAtCheckIn!!.firstName
                )
            )

        checkInDialogSuccess.show().also {
            coroutineScope.launch {
                delay(4000)
                checkInDialogSuccess.dismissWithAnimation()
            }
        }
    }

    private fun alternateTimeSelectionForArrivalTime() {
        TimePickerFragment().show(childFragmentManager, TimePickerFragment.TAG)

        lifecycleScope.launch {
            checkInViewModel.customSelectedTime.collect { customTimeSelection ->

                checkInViewBinding?.textviewArrivalTimeCustom?.visibility = View.VISIBLE
                checkInViewBinding?.textClockArrivalTime?.visibility = View.INVISIBLE

                checkInViewBinding?.textviewArrivalTimeCustom?.text =
                    parser.format(customTimeSelection.time)
            }
        }
    }

    private fun setupList() {
        allMembersAdapter = AttendanceRecyclerAdapter(::selectedAttendee)
        allMembersFragmentBinding.recyclerAllMembers.adapter = allMembersAdapter
    }

    private fun selectedAttendee(member: AttendeePresentation) {
        selectedAttendeeOptions = SelectedAttendeeOptionsBinding.inflate(layoutInflater)

        memberAtCheckIn = member

        val currentTime = Calendar.getInstance()

        val hourToSet = currentTime.get(Calendar.HOUR_OF_DAY)

        val finalHour = hourToSet - invalidationPeriod

        currentTime.set(Calendar.HOUR_OF_DAY, finalHour)

        selectedAttendeeOptions.selectedRegionName.text = member.name

        optionsPrompt?.dismiss()

        optionsPrompt =
            MaterialAlertDialogBuilder(requireContext()).setView(selectedAttendeeOptions.root)
                .show()

        val params = optionsPrompt?.window?.attributes
        params?.width = ViewGroup.LayoutParams.MATCH_PARENT

        optionsPrompt?.window?.attributes = params

        selectedAttendeeOptions.editAttendee.setOnClickListener {
            val navigateToEdit =
                MembersFragmentDirections.actionMembersFragmentToCreateMemberFragment(true, member)

            findNavController().navigate(navigateToEdit)

            optionsPrompt?.dismiss()
        }

        selectedAttendeeOptions.checkInAttendee.setOnClickListener {
            optionsPrompt?.dismiss()

            val lastStamp = member.lastCheckInStamp

            if (lastStamp != null) {
                if (lastStamp > currentTime.time.time) {
                    attemptingToCheckInAlreadyCheckedIn(member)
                    return@setOnClickListener
                }
            }

            checkInViewBinding = DialogLayoutCheckInBinding.inflate(layoutInflater)

            checkInPrompt =
                MaterialAlertDialogBuilder(requireContext()).setView(checkInViewBinding!!.root)
                    .show()

            listenToDialogClicks()
        }

    }

    private fun attemptingToCheckInAlreadyCheckedIn(member: AttendeePresentation) {

        SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
            .setTitleText(getString(R.string.already_checked_in_dialog_header))
            .setContentText(
                "${member.firstName} already checked-In ${
                    DateUtils.getRelativeTimeSpanString(
                        member.lastCheckInStamp!!
                    )
                }."
            )
            .setConfirmText(getString(R.string.undo_ckeckin_btn_text))
            .setConfirmClickListener { sDialog ->

                checkInViewModel.undoCheckInForMember(
                    CheckInEntity(
                        0,
                        member.memberId,
                        member.lastCheckInStamp,
                        0.0
                    )
                )

                sDialog.dismissWithAnimation()
            }
            .setCancelButton(
                getString(R.string.dialog_cancel)
            ) { sDialog -> sDialog.dismissWithAnimation() }
            .show()
    }

    private fun attemptingToCheckInInactiveMember(
        member: AttendeePresentation,
        checkInEntity: CheckInEntity
    ) {

        val addressing =
            if (member.sex == 1) " him " else if (member.sex == 0) " her " else " them "

        SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
            .setTitleText(getString(R.string.inactive_member_dialog_header))
            .setContentText(
                "${member.firstName} was previously marked as inactive. \n" +
                        "Would you wish to update as active and check $addressing In?"
            )
            .setConfirmText(getString(R.string.yes_option_dialog))
            .setConfirmClickListener { sDialog ->

                lifecycleScope.launch {
                    memberViewModel.updateMember(member.toMemberEntity().copy(isActive = true))
                }
                checkInViewModel.checkInMember(checkInEntity, member.toMemberEntity())

                sDialog.dismissWithAnimation()

                showCheckedInSuccess()

            }
            .setCancelButton(
                getString(R.string.no_option_dialog)
            ) { sDialog ->

                sDialog.dismissWithAnimation()

            }
            .show()
    }

    private fun watchStatuses() {
        lifecycleScope.launch(Dispatchers.IO) {
            allMembersViewModel.allMembersData.collect { membersAttendance ->
                if (membersAttendance.isEmpty()) {
                    withContext(Dispatchers.Main) {
                        allMembersFragmentBinding.noRegisteredMemberTextView.visibility =
                            View.VISIBLE
                        allMembersFragmentBinding.searchViewAllMembers.visibility = View.GONE
                    }
                } else {
                    val inactiveShouldBeHidden =
                        preferences.getBoolean(getString(R.string.inactive_members_pref_key), false)

                    val filteredList =
                        if (inactiveShouldBeHidden) membersAttendance.filter { memberEntry -> memberEntry.isActive } else membersAttendance

                    withContext(Dispatchers.Main) {
                        allMembersFragmentBinding.noRegisteredMemberTextView.visibility = View.GONE
                        allMembersFragmentBinding.searchViewAllMembers.visibility = View.VISIBLE


                        val memberText = resources.getQuantityString(
                            R.plurals.members_prefix,
                            filteredList.size,
                            filteredList.size
                        )

                        searchView.queryHint =
                            getString(R.string.filter_hint, memberText)

                        allMembersAdapter.modifyList(filteredList)

                        if (filteredList.isEmpty()) {
                            allMembersFragmentBinding.noRegisteredMemberTextView.visibility =
                                View.VISIBLE
                            allMembersFragmentBinding.searchViewAllMembers.visibility = View.GONE
                            val diff = membersAttendance - filteredList
                            val membersText = resources.getQuantityString(
                                R.plurals.members_prefix,
                                diff.size,
                                diff.size
                            )
                            val pluralAddress =
                                if (diff.size > 1) getString(R.string.text_are) else getString(R.string.text_is)
                            allMembersFragmentBinding.noRegisteredMemberTextView.text =
                                getString(R.string.no_active_member, membersText, pluralAddress)
                        }
                    }
                }
            }
        }


    }


}