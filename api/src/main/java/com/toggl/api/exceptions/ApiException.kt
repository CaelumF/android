package com.toggl.api.exceptions

abstract class ApiException(val errorMessage: String) : Exception() {
    private val badJsonLocalisedError = "Encountered unexpected error."

    companion object {
        private const val userAlreadyExistsApiErrorMessage = "user with this email already exists"

        fun from(statusCode: Int, localizedErrorMessage: String?, numberOfAttemptsLeft: Int?) =
            when (statusCode) {
                // Client errors
                ApiDeprecatedException.httpCode -> ApiDeprecatedException(localizedErrorMessage)
                BadRequestException.httpCode -> {
                    if (localizedErrorMessage == userAlreadyExistsApiErrorMessage) EmailIsAlreadyUsedException()
                    else BadRequestException(localizedErrorMessage)
                }
                ClientDeprecatedException.httpCode -> ClientDeprecatedException(localizedErrorMessage)
                ForbiddenException.httpCode -> ForbiddenException(localizedErrorMessage, numberOfAttemptsLeft)
                NotFoundException.httpCode -> NotFoundException(localizedErrorMessage)
                PaymentRequiredException.httpCode -> PaymentRequiredException(localizedErrorMessage)
                RequestEntityTooLargeException.httpCode -> RequestEntityTooLargeException(localizedErrorMessage)
                TooManyRequestsException.httpCode -> TooManyRequestsException(localizedErrorMessage)
                UnauthorizedException.httpCode -> UnauthorizedException(localizedErrorMessage)

                // Server errors
                InternalServerErrorException.httpCode -> InternalServerErrorException(localizedErrorMessage)
                NotImplementedException.httpCode -> NotImplementedException(localizedErrorMessage)
                BadGatewayException.httpCode -> BadGatewayException(localizedErrorMessage)
                ServiceUnavailableException.httpCode -> ServiceUnavailableException(localizedErrorMessage)
                GatewayTimeoutException.httpCode -> GatewayTimeoutException(localizedErrorMessage)
                HttpVersionNotSupportedException.httpCode -> HttpVersionNotSupportedException(localizedErrorMessage)

                else -> UnknownApiErrorException(statusCode, localizedErrorMessage)
            }
    }
}
