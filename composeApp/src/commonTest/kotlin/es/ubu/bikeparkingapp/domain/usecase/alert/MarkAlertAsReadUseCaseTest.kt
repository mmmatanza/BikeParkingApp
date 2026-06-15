package es.ubu.bikeparkingapp.domain.usecase.alert

import es.ubu.bikeparkingapp.helper.TestData
import es.ubu.bikeparkingapp.helper.repositories.FakeAlertRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

class MarkAlertAsReadUseCaseTest {

    private lateinit var repository: FakeAlertRepository
    private lateinit var useCase: MarkAlertAsReadUseCaseImpl

    @BeforeTest
    fun setUp() {
        repository = FakeAlertRepository()
        useCase = MarkAlertAsReadUseCaseImpl(repository)
    }

    @Test
    fun `Marcar alerta como leida actualiza el repositorio correctamente`() = runTest {

        val alert = TestData.testAlert.copy(alertId = "a1", isRead = false)
        repository.alerts.add(alert)


        val result = useCase("a1")


        assertTrue(result.isSuccess)
        assertTrue(repository.alerts[0].isRead)
    }

    @Test
    fun `Fallo en el repositorio devuelve error al marcar como leida`() = runTest {
        repository.shouldReturnError = true
        val result = useCase("a1")
        assertTrue(result.isFailure)
    }
}
