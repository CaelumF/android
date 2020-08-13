package com.toggl.repository.interfaces

import com.toggl.models.domain.User

interface UserRepository {
    suspend fun get(): User?
    suspend fun set(user: User)
    suspend fun update(updatedUser: User)
}
