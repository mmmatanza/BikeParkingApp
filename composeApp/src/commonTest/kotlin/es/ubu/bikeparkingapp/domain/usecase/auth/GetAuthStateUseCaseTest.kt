package es.ubu.bikeparkingapp.domain.usecase.auth

import es.ubu.bikeparkingapp.domain.model.AuthState
import es.ubu.bikeparkingapp.helper.repositories.FakeAuthRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetAuthStateUseCaseTest {

    private val fakeRepo = FakeAuthRepository()
    private val useCase = GetAuthStateUseCaseImpl(fakeRepo)

    @Test
    fun `Devuelve Unauthenticated por defecto`() = runTest {
        fakeRepo.authStateFlowValue = flowOf(AuthState.Unauthenticated)

        val result = useCase().first()

        assertEquals(AuthState.Unauthenticated, result)
    }

}