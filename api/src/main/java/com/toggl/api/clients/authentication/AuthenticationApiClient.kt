package com.toggl.api.clients.authentication

import com.toggl.models.domain.User
import com.toggl.models.validation.Email
import com.toggl.models.validation.Password

interface AuthenticationApiClient {
    suspend fun login(email: Email.Valid, password: Password.Valid): User
    suspend fun resetPassword(email: Email.Valid): String
    suspend fun signUp(email: Email.Valid, password: Password.Strong): User
}
