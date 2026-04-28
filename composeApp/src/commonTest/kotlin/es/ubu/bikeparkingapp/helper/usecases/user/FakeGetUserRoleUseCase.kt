package es.ubu.bikeparkingapp.helper.usecases.user

import es.ubu.bikeparkingapp.domain.entity.Role
import es.ubu.bikeparkingapp.domain.usecase.user.GetUserRoleUseCase

class FakeGetUserRoleUseCase : GetUserRoleUseCase {

    var response: Role = Role.USER

    var shouldFail = false
    var exception: Throwable = Exception("Fake error")

    override suspend operator fun invoke(): Result<Role> {

        return if (shouldFail) {
            Result.failure(exception)
        } else {
            Result.success(response)
        }
    }

}