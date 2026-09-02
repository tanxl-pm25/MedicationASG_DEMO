package com.example.medication_demo.model


import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

private val appointmentDateTimeFormatter =
    DateTimeFormatter.ofPattern(
        "dd MMM uuuu hh:mm a",
        Locale.ENGLISH
    )

private fun AppointmentUi.toDateTimeForSorting(): LocalDateTime {
    return try {
        LocalDateTime.parse(
            "$date $time",
            appointmentDateTimeFormatter
        )
    } catch (exception: Exception) { // catch (_: Exception)
        LocalDateTime.MAX
    }
}

data class AppointmentListUiState(
    val isUpcomingSelected: Boolean = true,
    val appointments: List<AppointmentUi> = emptyList()
) {
    val upcomingAppointments: List<AppointmentUi>
        get() = appointments
            .filter {
                it.status == AppointmentStatus.UPCOMING
            }
            .sortedBy {
                it.toDateTimeForSorting()
            }

    val historyAppointments: List<AppointmentUi>
        get() = appointments.filter {
            it.status != AppointmentStatus.UPCOMING
        }

    val actionNeededAppointments: List<AppointmentUi>
        get() = historyAppointments.filter {
            it.status == AppointmentStatus.MISSED ||
                    it.status == AppointmentStatus.CANCELLED
        }

    val resolvedAppointments: List<AppointmentUi>
        get() = historyAppointments.filter {
            it.status == AppointmentStatus.COMPLETED ||
                    it.status == AppointmentStatus.RESCHEDULED
        }



    val displayedAppointments: List<AppointmentUi>
        get() = if (isUpcomingSelected) {
            upcomingAppointments
        } else {
            historyAppointments
        }

    val selectedCount: Int
        get() = displayedAppointments.size
}