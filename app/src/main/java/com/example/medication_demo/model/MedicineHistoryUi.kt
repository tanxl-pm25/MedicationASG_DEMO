package com.example.medication_demo.model

data class MedicineHistoryUi(
    val name: String,
    val dosage: String,
    val time: String,
    val frequency: String,
    val medicineId: Int,
    val takenCount: Int,
    val missingCount: Int = 0,
    val presetImageRes: Int? = null,
    val galleryImageUri: String? = null
)