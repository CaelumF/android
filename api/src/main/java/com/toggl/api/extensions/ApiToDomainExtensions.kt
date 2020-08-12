package com.toggl.api.extensions

import com.toggl.api.models.ApiUser
import com.toggl.models.domain.User
import com.toggl.models.validation.ApiToken
import com.toggl.models.validation.Email

fun ApiUser.toModel() = User(
    id = id ?: userId ?: throw IllegalStateException(),
    name = fullname,
    email = Email.from(email) as Email.Valid,
    apiToken = ApiToken.from(apiToken) as ApiToken.Valid,
    defaultWorkspaceId = defaultWorkspaceId ?: 0
)
