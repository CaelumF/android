package com.toggl.database.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.toggl.database.properties.StringSyncProperty
import com.toggl.models.domain.WorkspaceFeature

@Entity(tableName = "workspaces")
data class DatabaseWorkspace(
    @PrimaryKey
    val id: Long = 0,
    val serverId: Long?,
    @Embedded(prefix = "name_")
    val name: StringSyncProperty,
    val features: List<WorkspaceFeature>
)
