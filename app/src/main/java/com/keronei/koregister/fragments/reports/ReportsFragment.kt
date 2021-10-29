package com.keronei.koregister.fragments.reports

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.keronei.kiregister.R
import com.keronei.kiregister.databinding.ReportsFragmentBinding
import com.keronei.koregister.fragments.checkin.DatePickerFragment
import com.keronei.koregister.viewmodels.AllMembersViewModel
import com.keronei.koregister.viewmodels.ReportsViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

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
                reportsViewModel.filterModel.emit(reportsViewModel.filterModel.value.copy(attendance = !checked ))
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

        lifecycleScope.launch {
            reportsViewModel.customSelectedDate.collect { selectedDate ->
                setMillis(selectedDate)

                displayDateSelected(selectedDate)
            }
        }
    }

    private fun showDateSelectionDialog() {
        DatePickerFragment().show(childFragmentManager, "date_picker")
    }

    private fun actionListeners() {
        reportsBinding.generateReportsFab.setOnClickListener {
            runGenerateReports()
        }
    }


    private fun runGenerateReports() {
        val generatedReport =
            reportsViewModel.filterModel.value.generateFinalReportWithFiltersApplied(
                allMembersViewModel.allMembersData.value
            )

        Log.d("GENERATED", "${generatedReport.size} in total.")
    }

}