package com.toggl.api.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiWorkspaceFeature(
    @Json(name = "workspace_id")
    val workspaceId: Long,
    val features: List<ApiFeatureValue>
)

@JsonClass(generateAdapter = true)
data class ApiFeatureValue(
    @Json(name = "feature_id")
    val featureId: Long,
    val name: String,
    val enabled: Boolean
)
