package es.ubu.bikeparkingapp.domain.usecase.reservation

import es.ubu.bikeparkingapp.helper.TestData
import es.ubu.bikeparkingapp.helper.repositories.FakeReservationRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Instant

class ExtendReservationUseCaseTest {

    private lateinit var reservationRepository: FakeReservationRepository
    private lateinit var useCase: ExtendReservationUseCaseImpl

    @BeforeTest
    fun setUp() {
        reservationRepository = FakeReservationRepository()
        useCase = ExtendReservationUseCaseImpl(reservationRepository)
    }

    @Test
    fun `Extension calcula la nueva hora y la envia al repositorio`() = runTest {
        // Preparación
        val resId = "res1"
        val outTimeOriginal = Instant.parse("2023-01-01T17:20:00Z")
        val extensionMinutes = 30
        // 17:20 + 30 min = 17:50
        val expectedOutTime = Instant.parse("2023-01-01T17:50:00Z")

        // Guardamos la reserva base en el fake
        reservationRepository.save(
            TestData.testReservation.copy(
            reservationId = resId,
            outTime = outTimeOriginal
        ))

        // Ejecución
        val result = useCase(resId, outTimeOriginal, extensionMinutes)

        // Assert
        assertTrue(result.isSuccess)

        // Verificamos que el repositorio recibió la hora calculada correctamente
        val updatedRes = reservationRepository.findById(resId).getOrThrow()
        assertEquals(expectedOutTime, updatedRes.outTime, "La hora calculada enviada al repo no es correcta")
    }

}