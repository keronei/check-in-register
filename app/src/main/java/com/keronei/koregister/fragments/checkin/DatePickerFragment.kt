package com.keronei.koregister.fragments.checkin

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.SharedPreferences
import android.os.Bundle
import android.text.format.DateFormat
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.keronei.kiregister.R
import com.keronei.koregister.viewmodels.CheckInViewModel
import com.keronei.utils.ToastUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

    private val checkInViewModel: CheckInViewModel by activityViewModels()

    @Inject
    lateinit var preferences: SharedPreferences

    var invalidationPeriod = 8

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current time as the default values for the picker
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        // Create a new instance of TimePickerDialog and return it
        return DatePickerDialog(requireContext(), this,year, month,day )

    }


    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
        val currentCalendar = Calendar.getInstance()

        val controlInstance = Calendar.getInstance()

        if (currentCalendar.after(controlInstance)) {
            ToastUtils.showLongToastInMiddle(R.string.arrival_time_alert)

            return
        }
    }


}