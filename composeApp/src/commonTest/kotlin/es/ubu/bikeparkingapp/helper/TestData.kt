package es.ubu.bikeparkingapp.helper

import es.ubu.bikeparkingapp.domain.entity.Account
import es.ubu.bikeparkingapp.domain.entity.Alert
import es.ubu.bikeparkingapp.domain.entity.AlertType
import es.ubu.bikeparkingapp.domain.entity.ParkingArea
import es.ubu.bikeparkingapp.domain.entity.Reservation
import es.ubu.bikeparkingapp.domain.entity.ReservationDetail
import es.ubu.bikeparkingapp.domain.entity.ReservationState
import es.ubu.bikeparkingapp.domain.entity.Role
import kotlinx.datetime.DayOfWeek
import kotlin.time.Instant

object TestData {
     val testAccount = Account(
        accountId = "user-123",
        name = "Manuel",
        taxId = "12345678A",
        role = Role.USER,
        createdAt = Instant.parse("2024-01-01T00:00:00Z"),
        updatedAt = Instant.parse("2024-01-01T00:00:00Z")
    )

    val testAdminAccount = Account(
        accountId = "admin-123",
        name = "Admin",
        taxId = "87654321B",
        role = Role.ADMIN,
        createdAt = Instant.parse("2024-01-01T00:00:00Z"),
        updatedAt = Instant.parse("2024-01-01T00:00:00Z")
    )

    val testReservation = Reservation(
        reservationId = "res1",
        parkingAreaId = "park1",
        accountId = "user123",
        inTime = Instant.parse("2023-01-01T16:20:00Z"),
        outTime = Instant.parse("2023-01-01T17:20:00Z"),
        state = ReservationState.RESERVED,
        createdAt = Instant.parse("2023-01-01T16:00:00Z")
    )

    val testParking = ParkingArea(
        parkingAreaId = "park1",
        name = "Parking Test",
        address = "Calle Test",
        latitude = 40.0,
        longitude = -3.0,
        ownerId = "admin-123",
        capacity = 10,
        currentOccupancy = 2,
        isOperative = true,
        isActive = true,
        openingTime = "00:00",
        closingTime = "23:59",
        openDays = setOf(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY),
        rules = listOf("Rule 1", "Rule 2"),
        timezoneId = "UTC"
    )

    val testReservationDetail = ReservationDetail(
        reservation = testReservation,
        parkingName = testParking.name,
        parkingAddress = testParking.address,
        parkingLatitude = testParking.latitude,
        parkingLongitude = testParking.longitude
    )

    val testAlert = Alert(
        alertId = "alert1",
        accountId = "user-123",
        parkingAreaId = "park1",
        parkingName = "Parking Test",
        reservationId = "res1",
        type = AlertType.OCCUPANCY_LIMIT,
        value = 90.0,
        customMessage = "High occupancy detected",
        isRead = false,
        createdAt = Instant.parse("2024-01-01T12:00:00Z")
    )
    
}