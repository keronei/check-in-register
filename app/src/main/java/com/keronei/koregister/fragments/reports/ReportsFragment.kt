package com.keronei.koregister.fragments.reports

import android.app.DatePickerDialog
import android.app.ProgressDialog.show
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.keronei.kiregister.R
import com.keronei.kiregister.databinding.ReportsFragmentBinding
import com.keronei.koregister.fragments.checkin.DatePickerFragment

class ReportsFragment : Fragment() {

    companion object {
        fun newInstance() = ReportsFragment()
    }

    lateinit var reportsBinding: ReportsFragmentBinding

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
    }

    private fun onFieldsChangedListeners() {
        reportsBinding.absentSelector.setOnCheckedChangeListener { _, b ->
            if (b) {
                reportsBinding.temperatureField.isEnabled = false
                reportsBinding.arrivalTimeField.isEnabled = false
            } else {
                reportsBinding.temperatureField.isEnabled = true
                reportsBinding.arrivalTimeField.isEnabled = true
            }
        }

        reportsBinding.dateSelectionTextview.setOnClickListener {
            showDateSelectionDialog()
        }
    }

    private fun showDateSelectionDialog() {
        DatePickerFragment().show(childFragmentManager, "date_picker")
    }


}