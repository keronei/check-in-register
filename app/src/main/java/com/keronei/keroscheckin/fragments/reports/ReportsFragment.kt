package com.keronei.keroscheckin.fragments.reports

import android.app.DatePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.RadioButton
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.android.material.textfield.TextInputEditText
import com.keronei.android.common.Constants.ALL_SEX_SELECTOR
import com.keronei.android.common.Constants.FEMALE_SELECTOR
import com.keronei.android.common.Constants.MALE_SELECTOR
import com.keronei.android.common.Constants.OTHER_SEX_SELECTOR
import com.keronei.domain.entities.AttendanceEntity
import com.keronei.keroscheckin.R
import com.keronei.keroscheckin.databinding.ReportsFragmentBinding
import com.keronei.keroscheckin.models.FieldsFilter
import com.keronei.keroscheckin.models.ReportsFilter
import com.keronei.keroscheckin.models.constants.CHECK_IN_INVALIDATE_DEFAULT_PERIOD
import com.keronei.keroscheckin.models.constants.ReportInclusion
import com.keronei.keroscheckin.models.toPresentation
import com.keronei.keroscheckin.viewmodels.AllMembersViewModel
import com.keronei.keroscheckin.viewmodels.ReportsViewModel
import com.keronei.utils.exportDataIntoWorkbook
import com.keronei.utils.makeShareIntent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

import java.lang.Exception
import javax.inject.Inject

@AndroidEntryPoint
class ReportsFragment : Fragment() {

    companion object {
        fun newInstance() = ReportsFragment()
    }

    private lateinit var reportsBinding: ReportsFragmentBinding
    private val reportsViewModel: ReportsViewModel by activityViewModels()
    private val allMembersViewModel: AllMembersViewModel by activityViewModels()
    private val parser = SimpleDateFormat("dd MMMM yyyy", Locale.US)
    private var invalidationPeriod = CHECK_IN_INVALIDATE_DEFAULT_PERIOD.toInt()

    private var startOfDateSelectedTimeStamp = 0L

    @Inject
    lateinit var preferences: SharedPreferences

    private fun resetFiltersOnViewCreated() {
        reportsViewModel.filterModel.value = ReportsFilter()

        FieldsFilter.clearAllInclusions()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        reportsBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.reports_fragment, container, false)

        val key = getString(R.string.invalidate_period_key)

        invalidationPeriod = preferences.getString(key, CHECK_IN_INVALIDATE_DEFAULT_PERIOD)?.toInt()
            ?: CHECK_IN_INVALIDATE_DEFAULT_PERIOD.toInt()


        return reportsBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onFieldsChangedListeners()

        actionListeners()

        watchFab()

        resetFiltersOnViewCreated()
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
        reportsBinding.reportsDate.setText(parser.format(date.time))
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

        reportsBinding.maleSelector.updateSexSelection(MALE_SELECTOR)
        reportsBinding.femaleSelector.updateSexSelection(FEMALE_SELECTOR)
        reportsBinding.allSexMembersSelector.updateSexSelection(ALL_SEX_SELECTOR)
        reportsBinding.checkInactiveMembers.setOnCheckedChangeListener { _, checked ->

            lifecycleScope.launch {
                reportsViewModel.filterModel.emit(
                    reportsViewModel.filterModel.value.copy(

                        includeInactive = checked

                    )
                )
            }

        }
        reportsBinding.reportsDate.transformToDateSelector()
        reportsBinding.phoneField.shouldInclude(ReportInclusion.PHONE)
        reportsBinding.idNumberField.shouldInclude(ReportInclusion.ID_NUMBER)
        reportsBinding.regionField.shouldInclude(ReportInclusion.REGION)
        reportsBinding.ageField.shouldInclude(ReportInclusion.AGE)
        reportsBinding.temperatureField.shouldInclude(ReportInclusion.TEMPERATURE)
        reportsBinding.arrivalTimeField.shouldInclude(ReportInclusion.CHECK_IN_TIME)

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

    private fun RadioButton.updateSexSelection(sexOrientation: Int) {
        this.setOnCheckedChangeListener { _, checked ->
            if (checked) {
                lifecycleScope.launch {
                    reportsViewModel.filterModel.emit(reportsViewModel.filterModel.value.copy(sex = sexOrientation))
                }
            }
        }

    }

    private fun CheckBox.shouldInclude(include: ReportInclusion) {
        this.setOnCheckedChangeListener { _, checked ->
            if (checked) {
                FieldsFilter.addInclusion(include)
            } else {
                FieldsFilter.removeInclusion(include)
            }
        }
    }

    private fun TextInputEditText.transformToDateSelector() {

        isFocusableInTouchMode = false
        isClickable = true
        isFocusable = false

        val myCalendar = Calendar.getInstance()
        val currentYear = myCalendar.get(Calendar.YEAR)
        val month = myCalendar.get(Calendar.MONTH)
        val day = myCalendar.get(Calendar.DAY_OF_MONTH)

        val datePickerOnDataSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                val controlInstance = Calendar.getInstance()

                controlInstance.set(Calendar.YEAR, year)
                controlInstance.set(Calendar.MONTH, monthOfYear)
                controlInstance.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                setText(parser.format(myCalendar.time))

                lifecycleScope.launch {
                    reportsViewModel.customSelectedDate.emit(controlInstance)
                }

            }
        setOnClickListener {

            DatePickerDialog(
                requireContext(),
                datePickerOnDataSetListener,
                currentYear,
                month,
                day
            ).run {
                myCalendar.time.time.also { date ->
                    datePicker.maxDate = date
                }

                show()
            }
        }
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
                allMembersViewModel.allMembersDataInEntities.value
            )


        if (generatedReport.isEmpty()) {
            SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText(getString(R.string.no_reports_header))
                .setContentText(
                    getString(R.string.no_report_message)
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
                generatedReport
            )

            val openingIntent = makeShareIntent(reportFileName, workBook, requireContext())

            reportsViewModel.preparedShareReportIntent.value = openingIntent

            val outputLauncher =
                ReportsFragmentDirections.actionReportsFragmentToReportsOutputFragment(
                    generatedReport.map { entry -> entry.toPresentation(invalidationPeriod) }
                        .toTypedArray()
                )

            findNavController().navigate(outputLauncher)

        } catch (exception: Exception) {
            Timber.log(Log.ERROR, "Error generating report.", exception)
            SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                .setTitleText(getString(R.string.error_dialog_header))
                .setContentText(
                    "Could not generate report. Report this error : ${exception.message}"
                )
                .show()
        }
    }


}