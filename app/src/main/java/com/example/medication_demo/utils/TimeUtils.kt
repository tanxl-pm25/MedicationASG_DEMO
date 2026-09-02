package com.example.medication_demo.utils

import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val MALAYSIA_ZONE = ZoneId.of("Asia/Kuala_Lumpur")

val MEDICINE_DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern(
        "dd MMM yyyy",
        Locale.ENGLISH
    )

val MEDICINE_TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern(
        "hh:mm a",
        Locale.ENGLISH
    )

fun getMalaysiaDate(): LocalDate {
    return LocalDate.now(MALAYSIA_ZONE)
}

fun getMalaysiaTime(): LocalTime {
    return LocalTime.now(MALAYSIA_ZONE)
}

fun parseMedicineDate(value: String): LocalDate? {
    return try {
        LocalDate.parse(
            value,
            MEDICINE_DATE_FORMATTER
        )
    } catch (_: Exception) {
        null
    }
}

fun parseMedicineTime(value: String): LocalTime? {
    return try {
        LocalTime.parse(
            value,
            MEDICINE_TIME_FORMATTER
        )
    } catch (_: Exception) {
        null
    }
}