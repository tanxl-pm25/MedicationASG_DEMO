package com.example.medication_demo.viewmodel

import androidx.lifecycle.ViewModel
import com.example.medication_demo.model.Medicine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MedicineListViewModel : ViewModel() {
    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText.asStateFlow()

    private val _selectedFilter = MutableStateFlow("All")
    val selectedFilter: StateFlow<String> = _selectedFilter.asStateFlow()


    fun onSearchTextChange(value: String) {
        _searchText.value = value
    }

    fun onFilterSelected(value: String) {
        _selectedFilter.value = value
    }

    fun filterMedicines(
        medicines: List<Medicine>,
        searchText: String,
        selectedFilter: String
    ): List<Medicine> {

        return medicines.filter { medicine ->

            val matchesSearch = medicine.name.contains(
                    searchText,
                    ignoreCase = true
                )
            val matchesFilter = when (selectedFilter) {
                "All" -> true
                "Active" -> true
                "Upcoming" -> true
                "Completed" -> true
                else -> true
            }
            matchesSearch && matchesFilter
        }
    }


}

