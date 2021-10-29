package com.keronei.koregister.viewmodels

import androidx.lifecycle.ViewModel
import com.keronei.koregister.models.FieldsFilter
import com.keronei.koregister.models.ReportsFilter
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.*


class ReportsViewModel : ViewModel() {
    val customSelectedDate = MutableStateFlow(value = Calendar.getInstance())

    val filterModel = MutableStateFlow(value = ReportsFilter())
    val fieldsFilterModel = MutableStateFlow(value = FieldsFilter())

}