package com.example.medication_demo.utils

import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

private val MALAYSIA_ZONE =
    ZoneId.of("Asia/Kuala_Lumpur")

fun getMalaysiaDate(): LocalDate {
    return LocalDate.now(MALAYSIA_ZONE)
}

fun getMalaysiaTime(): LocalTime {
    return LocalTime.now(MALAYSIA_ZONE)
}