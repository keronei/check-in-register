package com.keronei.koregister.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.*


class ReportsViewModel : ViewModel() {
    val customSelectedDate = MutableStateFlow(value = Calendar.getInstance())
}