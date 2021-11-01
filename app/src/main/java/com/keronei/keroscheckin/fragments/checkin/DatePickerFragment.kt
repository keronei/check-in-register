package com.keronei.keroscheckin.fragments.checkin

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.keronei.kiregister.R
import com.keronei.keroscheckin.viewmodels.ReportsViewModel
import com.keronei.utils.ToastUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

    private val reportsViewModel: ReportsViewModel by activityViewModels()


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current time as the default values for the picker
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        // Create a new instance of TimePickerDialog and return it
        return DatePickerDialog(requireContext(), this, year, month, day)

    }


    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, day: Int) {
        val selectedCalendar = Calendar.getInstance()
        selectedCalendar.set(Calendar.YEAR, year)
        selectedCalendar.set(Calendar.MONTH, month)
        selectedCalendar.set(Calendar.DAY_OF_MONTH, day)


        val controlInstance = Calendar.getInstance()

        if (selectedCalendar.after(controlInstance)) {
            ToastUtils.showLongToastInMiddle(R.string.arrival_time_alert)

            return
        }

        lifecycleScope.launch {
            reportsViewModel.customSelectedDate.emit(selectedCalendar)
        }

    }
}