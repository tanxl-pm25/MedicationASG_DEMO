package com.example.medication_demo.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NotificationNavigationViewModel : ViewModel() {

    private val _refillMedicineId =
        MutableStateFlow<Int?>(null)

    val refillMedicineId:
            StateFlow<Int?> =
        _refillMedicineId.asStateFlow()

    fun openRefillReminder(
        medicineId: Int
    ) {
        _refillMedicineId.value =
            medicineId
    }

    fun clearRefillNavigation() {
        _refillMedicineId.value = null
    }
}