package com.keronei.koregister.fragments.checkin

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.SharedPreferences
import android.os.Bundle
import android.text.format.DateFormat
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
class TimePickerFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    private val checkInViewModel: CheckInViewModel by activityViewModels()

    @Inject
    lateinit var preferences: SharedPreferences

    var invalidationPeriod = 8

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current time as the default values for the picker
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        invalidationPeriod =
            preferences.getString(getString(R.string.invalidate_period_key), "8")?.toInt() ?: 8

        // Create a new instance of TimePickerDialog and return it
        return TimePickerDialog(activity, this, hour, minute, false)

    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        // Do something with the time chosen by the user

        val currentCalendar = Calendar.getInstance()

        currentCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        currentCalendar.set(Calendar.MINUTE, minute)

        val controlInstance = Calendar.getInstance()

        if (currentCalendar.after(controlInstance)) {
            ToastUtils.showLongToastInMiddle(R.string.arrival_time_alert)

            return
        }

        val hourToSet = controlInstance.get(Calendar.HOUR_OF_DAY)

        val finalHour = hourToSet - invalidationPeriod

        controlInstance.set(Calendar.HOUR_OF_DAY, finalHour)

        if (currentCalendar.before(controlInstance)) {
            MaterialAlertDialogBuilder(requireContext()).setMessage(R.string.too_long_ago).show()
            return
        }

        lifecycleScope.launch {
            checkInViewModel.customSelectedTime.emit(currentCalendar)
        }
    }
}