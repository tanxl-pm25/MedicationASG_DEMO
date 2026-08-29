package com.example.medication_demo.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class BottomNavigationViewModel : ViewModel() {

    private val _selectedIndex = MutableStateFlow(0)
    val selectedIndex: StateFlow<Int> =
        _selectedIndex.asStateFlow()

    fun onItemSelected(index: Int) {
        _selectedIndex.value = index
    }

    fun setCurrentScreen(index: Int) {
        _selectedIndex.value = index
    }
}