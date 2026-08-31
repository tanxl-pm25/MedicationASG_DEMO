package com.example.medication_demo.model

enum class DoseStatus(
    val displayText: String
) {
    TAKEN("Taken"),
    MISSING("Missing"),
    UPCOMING("Upcoming"),
    IN_PROGRESS("In Progress")
}