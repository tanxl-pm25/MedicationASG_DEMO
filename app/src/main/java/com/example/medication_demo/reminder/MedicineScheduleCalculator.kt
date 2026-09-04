package com.example.medication_demo.reminder

import com.example.medication_demo.model.Medicine
import com.example.medication_demo.model.NextScheduledDose
import com.example.medication_demo.utils.parseMedicineDate
import com.example.medication_demo.utils.parseMedicineTime
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.temporal.ChronoUnit
import java.time.format.DateTimeFormatter
import java.util.Locale

fun isReminderDateForMedicine(
    medicine: Medicine,
    date: LocalDate,
    startDate: LocalDate
): Boolean {

    if (date.isBefore(startDate)) {
        return false
    }

    val frequency =
        medicine.frequency
            .trim()
            .lowercase()

    return when (frequency) {

        "once a day",
        "twice a day",
        "3 times a day" -> {
            true
        }

        "once a week" -> {

            val daysBetween =
                ChronoUnit.DAYS.between(
                    startDate,
                    date
                )

            daysBetween % 7L == 0L
        }

        else -> {

            val regex =
                Regex(
                    """every\s+(\d+)\s+(day|days|week|weeks|month|months)"""
                )

            val match =
                regex.find(
                    frequency
                ) ?: return false

            val amount =
                match.groupValues[1]
                    .toLongOrNull()
                    ?: return false

            if (amount <= 0) {
                return false
            }

            when (
                match.groupValues[2]
            ) {

                "day",
                "days" -> {

                    val daysBetween =
                        ChronoUnit.DAYS.between(
                            startDate,
                            date
                        )

                    daysBetween % amount == 0L
                }

                "week",
                "weeks" -> {

                    val daysBetween =
                        ChronoUnit.DAYS.between(
                            startDate,
                            date
                        )

                    daysBetween %
                            (amount * 7L) == 0L
                }

                "month",
                "months" -> {

                    val startMonth =
                        YearMonth.from(startDate)

                    val currentMonth =
                        YearMonth.from(date)

                    val monthsBetween =
                        ChronoUnit.MONTHS.between(
                            startMonth,
                            currentMonth
                        )

                    if (
                        monthsBetween % amount != 0L
                    ) {
                        false
                    } else {

                        val expectedDay =
                            minOf(
                                startDate.dayOfMonth,
                                currentMonth.lengthOfMonth()
                            )

                        date.dayOfMonth ==
                                expectedDay
                    }
                }

                else -> false
            }
        }
    }
}

private fun getMedicineTotalDoses(
    medicine: Medicine
): Long {

    val quantity =
        medicine.quantity
            .toDoubleOrNull()
            ?: return 0L

    val dosage =
        medicine.dosageAmount
            .toDoubleOrNull()
            ?: return 0L

    if (
        quantity <= 0 ||
        dosage <= 0
    ) {
        return 0L
    }

    return (quantity / dosage)
        .toLong()
}

fun findNextScheduledDose(
    medicine: Medicine,
    afterDateTime: LocalDateTime
): NextScheduledDose? {

    if (!medicine.reminderEnabled) {
        return null
    }

    if (
        medicine.frequency.equals(
            "As needed",
            ignoreCase = true
        )
    ) {
        return null
    }

    val totalDoses =
        getMedicineTotalDoses(
            medicine
        )

    if (totalDoses <= 0L) {
        return null
    }

    val startDate =
        parseMedicineDate(
            medicine.startDate
        ) ?: return null

    val reminderTimes =
        medicine.reminderTimes
            .mapIndexedNotNull { index, reminder ->

                val time =
                    parseMedicineTime(
                        reminder.time
                    ) ?: return@mapIndexedNotNull null

                Triple(
                    index,
                    reminder.time,
                    time
                )
            }
            .sortedBy {
                it.third
            }

    if (reminderTimes.isEmpty()) {
        return null
    }

    val frequency =
        medicine.frequency
            .trim()
            .lowercase()

    // =============================
    // EVERY N HOURS
    // =============================

    val hourlyMatch =
        Regex(
            """every\s+(\d+)\s+(hour|hours)"""
        ).find(
            frequency
        )

    if (hourlyMatch != null) {

        val intervalHours =
            hourlyMatch
                .groupValues[1]
                .toLongOrNull()
                ?: return null

        if (intervalHours <= 0) {
            return null
        }

        val firstReminder =
            reminderTimes.first()

        var nextDateTime =
            startDate.atTime(
                firstReminder.third
            )
        var doseNumber = 1L
        while (
            !nextDateTime.isAfter(
                afterDateTime
            )
        ) {

            nextDateTime =
                nextDateTime.plusHours(
                    intervalHours
                )

            doseNumber++

            if (doseNumber > totalDoses) {
                return null
            }
        }

        return NextScheduledDose(
            doseDate =
                nextDateTime.toLocalDate(),

            doseIndex = 0,

            scheduledTime =
                nextDateTime
                    .toLocalTime()
                    .format(
                        DateTimeFormatter.ofPattern(
                            "hh:mm a",
                            Locale.ENGLISH
                        )
                    ),

            scheduledLocalTime =
                nextDateTime.toLocalTime()
        )
    }

    // =============================
    // OTHER FREQUENCIES
    // =============================

    var candidateDate =
        if (
            afterDateTime
                .toLocalDate()
                .isBefore(startDate)
        ) {
            startDate
        } else {
            afterDateTime.toLocalDate()
        }

    var doseNumber = 0L

    repeat(370) {

        if (
            isReminderDateForMedicine(
                medicine = medicine,
                date = candidateDate,
                startDate = startDate
            )
        ) {

            reminderTimes.forEach { reminder ->

                doseNumber++

                if (doseNumber > totalDoses) {
                    return null
                }

                val candidateDateTime =
                    candidateDate.atTime(
                        reminder.third
                    )

                if (
                    candidateDateTime.isAfter(
                        afterDateTime
                    )
                ) {
                    return NextScheduledDose(
                        doseDate = candidateDate,
                        doseIndex = reminder.first,
                        scheduledTime = reminder.second,
                        scheduledLocalTime = reminder.third
                    )
                }
            }
        }

        candidateDate =
            candidateDate.plusDays(1)
    }

    return null
}