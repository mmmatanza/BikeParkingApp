package es.ubu.bikeparkingapp.helper.usecases.auth

import es.ubu.bikeparkingapp.domain.model.AuthState
import es.ubu.bikeparkingapp.domain.usecase.auth.GetAuthStateUseCase
import kotlinx.coroutines.flow.flowOf

class FakeGetAuthStateUseCase : GetAuthStateUseCase {
    override fun invoke() = flowOf(AuthState.Unauthenticated)
}
