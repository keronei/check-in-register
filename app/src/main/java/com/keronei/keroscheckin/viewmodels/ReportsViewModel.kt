package com.keronei.keroscheckin.viewmodels

import android.content.Intent
import androidx.lifecycle.ViewModel
import com.keronei.keroscheckin.models.FieldsFilter
import com.keronei.keroscheckin.models.ReportsFilter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import java.util.*


class ReportsViewModel : ViewModel() {
    val customSelectedDate = MutableStateFlow(value = Calendar.getInstance())

    val filterModel = MutableStateFlow(value = ReportsFilter())

    val preparedShareReportIntent = MutableStateFlow(value = Intent())

}