package es.ubu.bikeparkingapp.domain.usecase.parking

import es.ubu.bikeparkingapp.helper.TestData
import es.ubu.bikeparkingapp.helper.repositories.FakeAuthRepository
import es.ubu.bikeparkingapp.helper.repositories.FakeParkingAreaRepository
import es.ubu.bikeparkingapp.helper.repositories.FakeReservationRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GetParkingDiscoveryUseCaseTest {

    private lateinit var parkingRepository: FakeParkingAreaRepository
    private lateinit var reservationRepository: FakeReservationRepository
    private lateinit var authRepository: FakeAuthRepository
    private lateinit var useCase: GetParkingDiscoveryUseCaseImpl

    @BeforeTest
    fun setUp() {
        parkingRepository = FakeParkingAreaRepository()
        reservationRepository = FakeReservationRepository()
        authRepository = FakeAuthRepository()
        useCase = GetParkingDiscoveryUseCaseImpl(
            parkingRepository,
            reservationRepository,
            authRepository
        )
    }

    @Test
    fun `Cuando no hay parkings cercanos devuelve discovery vacio`() = runTest {
        val result = useCase(40.0, -3.0, 1000.0)

        assertTrue(result.isSuccess)
        val discovery = result.getOrThrow()
        assertNull(discovery.recommended)
        assertTrue(discovery.allNearby.isEmpty())
    }

    @Test
    fun `El parking recomendado es el mas cercano si no hay historial de visitas`() = runTest {

        val p1 = TestData.testParking.copy(parkingAreaId = "p1", latitude = 40.0009, longitude = -3.0, capacity = 10, currentOccupancy = 0)
        val p2 = TestData.testParking.copy(parkingAreaId = "p2", latitude = 40.0045, longitude = -3.0, capacity = 10, currentOccupancy = 0)
        
        parkingRepository.addParkingArea(p1)
        parkingRepository.addParkingArea(p2)
        authRepository.currentUserIdResult = Result.success("user123")

        val result = useCase(40.0, -3.0, 1000.0)

        assertTrue(result.isSuccess)
        val discovery = result.getOrThrow()
        assertEquals("p1", discovery.recommended?.parkingAreaId)
        assertEquals(1, discovery.allNearby.size)
        assertEquals("p2", discovery.allNearby[0].parkingAreaId)
    }

    @Test
    fun `El parking con mas visitas puede superar a uno mas cercano`() = runTest {
        val p1 = TestData.testParking.copy(parkingAreaId = "p1", latitude = 40.0009, longitude = -3.0, capacity = 10, currentOccupancy = 0)
        val p2 = TestData.testParking.copy(parkingAreaId = "p2", latitude = 40.0045, longitude = -3.0, capacity = 10, currentOccupancy = 0)
        
        parkingRepository.addParkingArea(p1)
        parkingRepository.addParkingArea(p2)
        
        authRepository.currentUserIdResult = Result.success("user123")
        
        reservationRepository.save(TestData.testReservation.copy(
            parkingAreaId = "p2", 
            accountId = "user123", 
            state = es.ubu.bikeparkingapp.domain.entity.ReservationState.CHECKED_OUT
        ))

        val result = useCase(40.0, -3.0, 1000.0)

        assertTrue(result.isSuccess)
        val discovery = result.getOrThrow()

        assertEquals("p2", discovery.recommended?.parkingAreaId)
    }

    @Test
    fun `Si un parking esta lleno no puede ser el recomendado`() = runTest {

        val p1 = TestData.testParking.copy(parkingAreaId = "p1", latitude = 40.0009, longitude = -3.0, capacity = 10, currentOccupancy = 10)
        val p2 = TestData.testParking.copy(parkingAreaId = "p2", latitude = 40.0045, longitude = -3.0, capacity = 10, currentOccupancy = 0)
        
        parkingRepository.addParkingArea(p1)
        parkingRepository.addParkingArea(p2)

        val result = useCase(40.0, -3.0, 1000.0)

        assertTrue(result.isSuccess)
        val discovery = result.getOrThrow()
        assertEquals("p2", discovery.recommended?.parkingAreaId)
    }
}
