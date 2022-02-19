package com.keronei.keroscheckin.fragments.reports

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.keronei.keroscheckin.R
import com.keronei.keroscheckin.databinding.ReportsFragmentBinding
import com.keronei.keroscheckin.fragments.checkin.DatePickerFragment
import com.keronei.keroscheckin.viewmodels.AllMembersViewModel
import com.keronei.keroscheckin.viewmodels.ReportsViewModel
import com.keronei.utils.exportDataIntoWorkbook
import com.keronei.utils.makeShareIntent
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

import java.lang.Exception


class ReportsFragment : Fragment() {

    companion object {
        fun newInstance() = ReportsFragment()
    }

    lateinit var reportsBinding: ReportsFragmentBinding
    private val reportsViewModel: ReportsViewModel by activityViewModels()
    private val allMembersViewModel: AllMembersViewModel by activityViewModels()
    private val parser = SimpleDateFormat("dd - MMM - yyyy", Locale.US)

    private var startOfDateSelectedTimeStamp = 0L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        reportsBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.reports_fragment, container, false)

        return reportsBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onFieldsChangedListeners()

        actionListeners()

        watchFab()
    }

    private fun watchFab() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            reportsBinding.nestedScrollViewReports.setOnScrollChangeListener { _, scrollX, scrollY, _, oldScrollY ->
                with(reportsBinding.chatFabText) {
                    visibility = when {
                        scrollY > oldScrollY -> {
                            View.VISIBLE
                        }
                        scrollX == scrollY -> {
                            View.GONE
                        }
                        else -> {
                            View.VISIBLE
                        }
                    }
                }
            }
        }
    }

    private fun setMillis(value: Calendar) {
        value.set(Calendar.HOUR_OF_DAY, 0)
        value.set(Calendar.MINUTE, 0)
        value.set(Calendar.SECOND, 0)

        lifecycleScope.launch {
            reportsViewModel.filterModel.emit(reportsViewModel.filterModel.value.copy(selectedDate = value))
        }

        startOfDateSelectedTimeStamp = value.timeInMillis
    }

    private fun displayDateSelected(date: Calendar) {
        reportsBinding.dateSelectionTextview.text = parser.format(date.time)
    }

    private fun onFieldsChangedListeners() {
        val currentDate = Calendar.getInstance()

        setMillis(currentDate)

        displayDateSelected(currentDate)

        reportsBinding.absentSelector.setOnCheckedChangeListener { _, checked ->


            lifecycleScope.launch {
                reportsViewModel.filterModel.emit(reportsViewModel.filterModel.value.copy(attendance = !checked))
            }


            if (checked) {
                reportsBinding.temperatureField.isEnabled = false
                reportsBinding.arrivalTimeField.isEnabled = false
                reportsBinding.temperatureField.isChecked = false
                reportsBinding.arrivalTimeField.isChecked = false

            } else {
                reportsBinding.temperatureField.isEnabled = true
                reportsBinding.arrivalTimeField.isEnabled = true
            }
        }

        reportsBinding.presentSelector.setOnCheckedChangeListener { _, checked ->

            lifecycleScope.launch {
                reportsViewModel.filterModel.emit(
                    reportsViewModel.filterModel.value.copy(
                        attendance = checked
                    )
                )
            }


        }

        reportsBinding.maleSelector.setOnCheckedChangeListener { _, checked ->
            if (checked) {
                lifecycleScope.launch {
                    reportsViewModel.filterModel.emit(reportsViewModel.filterModel.value.copy(sex = 1))
                }
            }
        }

        reportsBinding.femaleSelector.setOnCheckedChangeListener { _, checked ->
            if (checked) {
                lifecycleScope.launch {
                    reportsViewModel.filterModel.emit(reportsViewModel.filterModel.value.copy(sex = 0))
                }
            }

        }

        reportsBinding.allSexMembersSelector.setOnCheckedChangeListener { _, checked ->
            if (checked) {
                lifecycleScope.launch {
                    reportsViewModel.filterModel.emit(reportsViewModel.filterModel.value.copy(sex = 2))
                }

            }
        }

        reportsBinding.checkInactiveMembers.setOnCheckedChangeListener { _, checked ->

            lifecycleScope.launch {
                reportsViewModel.filterModel.emit(
                    reportsViewModel.filterModel.value.copy(

                        includeInactive = checked

                    )
                )
            }

        }

        reportsBinding.dateSelectionTextview.setOnClickListener {
            showDateSelectionDialog()
        }

        reportsBinding.phoneField.setOnCheckedChangeListener { _, checked ->

            lifecycleScope.launch {
                reportsViewModel.fieldsFilterModel.emit(
                    value = reportsViewModel.fieldsFilterModel.value.copy(
                        includePhone = checked
                    )
                )
            }

        }

        reportsBinding.regionField.setOnCheckedChangeListener { _, checked ->
            lifecycleScope.launch {
                reportsViewModel.fieldsFilterModel.emit(
                    value = reportsViewModel.fieldsFilterModel.value.copy(
                        includeRegion = checked
                    )
                )
            }
        }

        reportsBinding.ageField.setOnCheckedChangeListener { _, checked ->
            lifecycleScope.launch {
                reportsViewModel.fieldsFilterModel.emit(
                    value = reportsViewModel.fieldsFilterModel.value.copy(
                        includeAge = checked
                    )
                )
            }
        }

        reportsBinding.temperatureField.setOnCheckedChangeListener { _, checked ->
            lifecycleScope.launch {
                reportsViewModel.fieldsFilterModel.emit(
                    value = reportsViewModel.fieldsFilterModel.value.copy(
                        includeTemperature = checked
                    )
                )
            }
        }

        reportsBinding.arrivalTimeField.setOnCheckedChangeListener { _, checked ->
            lifecycleScope.launch {
                reportsViewModel.fieldsFilterModel.emit(
                    value = reportsViewModel.fieldsFilterModel.value.copy(
                        includeCheckInTime = checked
                    )
                )
            }
        }

        lifecycleScope.launch {
            reportsViewModel.customSelectedDate.collect { selectedDateFromCalendar ->
                setMillis(selectedDateFromCalendar)

                displayDateSelected(selectedDateFromCalendar)

                reportsViewModel.filterModel.emit(
                    reportsViewModel.filterModel.value.copy(
                        selectedDate = selectedDateFromCalendar
                    )
                )

            }
        }
    }

    private fun showDateSelectionDialog() {
        DatePickerFragment().show(childFragmentManager, "date_picker")
    }

    private fun actionListeners() {
        reportsBinding.fabFullBtnReport.setOnClickListener {
            runGenerateReports()
        }

        reportsBinding.createNewMember.setOnClickListener {
            runGenerateReports()
        }
    }


    private fun runGenerateReports() {
        val generatedReport =
            reportsViewModel.filterModel.value.generateFinalReportWithFiltersApplied(
                allMembersViewModel.allMembersData.value
            )


        if (generatedReport.isEmpty()) {
            SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText("No Reports")
                .setContentText(
                    "No entries matching your selection criteria."
                )
                .show()

            return
        }

        val reportFileName =
            if (reportsViewModel.filterModel.value.attendance) "Members present on ${
                parser.format(Date(startOfDateSelectedTimeStamp))
            } report"
            else
                "Members absent on ${parser.format(Date(startOfDateSelectedTimeStamp))} report"

        try {
            val workBook = exportDataIntoWorkbook(
                reportFileName,
                generatedReport,
                reportsViewModel.fieldsFilterModel.value
            )

            val openingIntent = makeShareIntent(reportFileName, workBook, requireContext())

          val reportsOutputFragment =  ReportsOutputFragment()

            reportsOutputFragment.show(childFragmentManager, ReportsOutputFragment.TAG)


//            MaterialAlertDialogBuilder(requireContext()).setMessage(
//                resources.getQuantityString(
//                    R.plurals.reports_generated,
//                    generatedReport.size,
//                    generatedReport.size
//                )
//            )
//
//                .setNegativeButton("Share") { _, _ ->
//                    openingIntent.action = Intent.ACTION_SEND
//                    startActivity(Intent.createChooser(openingIntent, "Share report"))
//
//                }.setPositiveButton("View") { _, _ ->
//
//                    openingIntent.action = Intent.ACTION_VIEW
//
//                    try {
//                        startActivity(openingIntent)
//                    } catch (openingException: Exception) {
//
//                        openingException.printStackTrace()
//
//                        SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
//                            .setTitleText("Error")
//                            .setContentText(
//                                "No app found in your phone that can open this Excel report."
//                            )
//                            .show()
//
//                        Log.d("TAG", "No Intent available to handle action")
//                    }
//
//                }
//                .show()


        } catch (exception: Exception) {
            SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Error")
                .setContentText(
                    "Could not generate report. Report this error : ${exception.message}"
                )
                .show()
        }
    }


}