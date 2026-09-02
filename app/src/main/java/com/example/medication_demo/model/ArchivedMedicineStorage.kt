package com.example.medication_demo.model

import kotlinx.serialization.Serializable

@Serializable
data class ArchivedMedicineStorage(
    val medicine: Medicine,
    val deletedDate: String,
    val deletedTime: String
)