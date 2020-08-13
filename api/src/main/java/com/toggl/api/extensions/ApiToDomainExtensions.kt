package com.toggl.api.extensions

import com.toggl.api.models.ApiProject
import com.toggl.api.models.ApiUser
import com.toggl.models.domain.Project
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

fun ApiProject.toModel() = Project(
    id = id,
    name = name,
    color = color,
    active = active,
    isPrivate = isPrivate,
    billable = billable,
    workspaceId = workspaceId,
    clientId = clientId
)
