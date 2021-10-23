package com.keronei.koregister.fragments.checkin

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.keronei.kiregister.R
import com.keronei.koregister.viewmodels.CheckInViewModel
import com.keronei.utils.ToastUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class TimePickerFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    private val checkInViewModel: CheckInViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current time as the default values for the picker
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        // Create a new instance of TimePickerDialog and return it
        return TimePickerDialog(activity, this, hour, minute, false)

    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        // Do something with the time chosen by the user

        val currentCalendar = Calendar.getInstance()

        currentCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        currentCalendar.set(Calendar.MINUTE, minute)

        val controlInstance = Calendar.getInstance()

        if (currentCalendar.after(controlInstance)){
            ToastUtils.showLongToastInMiddle(R.string.arrival_time_alert)

            return
        }

        lifecycleScope.launch {
            checkInViewModel.customSelectedTime.emit(currentCalendar)
        }
    }
}