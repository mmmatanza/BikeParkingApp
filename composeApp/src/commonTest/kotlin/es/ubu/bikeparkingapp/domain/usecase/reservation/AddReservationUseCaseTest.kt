package es.ubu.bikeparkingapp.domain.usecase.reservation

import es.ubu.bikeparkingapp.domain.entity.ReservationState
import es.ubu.bikeparkingapp.domain.exception.AccountHasActiveReservationException
import es.ubu.bikeparkingapp.domain.exception.ParkingHasNoFreeSpotsException
import es.ubu.bikeparkingapp.domain.exception.ParkingIsClosedException
import es.ubu.bikeparkingapp.helper.TestData
import es.ubu.bikeparkingapp.helper.repositories.FakeParkingAreaRepository
import es.ubu.bikeparkingapp.helper.repositories.FakeReservationRepository
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Clock

class AddReservationUseCaseTest {

    private lateinit var reservationRepository: FakeReservationRepository
    private lateinit var parkingRepository: FakeParkingAreaRepository
    private lateinit var useCase: AddReservationUseCaseImpl

    @BeforeTest
    fun setUp() {
        reservationRepository = FakeReservationRepository()
        parkingRepository = FakeParkingAreaRepository()
        useCase = AddReservationUseCaseImpl(reservationRepository, parkingRepository)
    }

    @Test
    fun `Reserva exitosa guarda la reserva en el repositorio`() = runTest {
        // Preparación
        val parkingId = "park1"
        val userId = "user123"

        parkingRepository.addParkingArea(TestData.testParking.copy(
            parkingAreaId = parkingId,
            capacity = 10,
            currentOccupancy = 0,
            openingTime = "00:00",
            closingTime = "23:59",
            openDays = DayOfWeek.entries.toSet(),
            timezoneId = "UTC"
        ))

        // Ejecución
        val result = useCase(parkingId, userId)

        // Assert
        assertTrue(result.isSuccess, "Fallo: ${result.exceptionOrNull()}")
        val active = reservationRepository.findByAccountId(userId).getOrThrow()
        assertEquals(1, active.size)
    }

    @Test
    fun `Fallo cuando el usuario ya tiene una reserva activa`() = runTest {
        // Preparación
        val userId = "user123"
        val parkingId = "park1"

        // Añadimos el parking para que no falle por "not found" si pasara la primera validación
        parkingRepository.addParkingArea(TestData.testParking.copy(parkingAreaId = parkingId))

        // Guardamos una reserva activa en el fake
        reservationRepository.save(TestData.testReservation.copy(
            accountId = userId,
            state = ReservationState.RESERVED
        ))

        // Ejecución
        val result = useCase(parkingId, userId)

        // Assert
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AccountHasActiveReservationException)
    }

    @Test
    fun `Fallo cuando el parking no tiene plazas libres`() = runTest {
        // Preparación
        val parkingId = "park_lleno"
        parkingRepository.addParkingArea(TestData.testParking.copy(
            parkingAreaId = parkingId,
            capacity = 5,
            currentOccupancy = 5,
            openingTime = "00:00",
            closingTime = "23:59",
            openDays = DayOfWeek.entries.toSet(),
            timezoneId = "UTC"
        ))

        // Ejecución
        val result = useCase(parkingId, "user1")

        // Assert
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is ParkingHasNoFreeSpotsException)
    }

    @Test
    fun `Fallo cuando hoy es un dia que el parking cierra`() = runTest {
        // Preparación
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val hoy = now.dayOfWeek
        // Todos los días menos hoy
        val diasAbiertos = DayOfWeek.entries.filter { it != hoy }.toSet()

        parkingRepository.addParkingArea(TestData.testParking.copy(
            parkingAreaId = "park_cerrado",
            openDays = diasAbiertos,
            openingTime = "00:00",
            closingTime = "23:59",
            timezoneId = TimeZone.currentSystemDefault().id
        ))

        // Ejecución
        val result = useCase("park_cerrado", "user1")

        // Assert
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is ParkingIsClosedException)
    }
}