package com.toggl.database.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.toggl.database.properties.BooleanSyncProperty
import com.toggl.database.properties.LongSyncProperty
import com.toggl.database.properties.NullableLongSyncProperty
import com.toggl.database.properties.StringSyncProperty

@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(entity = DatabaseWorkspace::class, parentColumns = ["id"], childColumns = ["workspaceId_current"]),
        ForeignKey(entity = DatabaseProject::class, parentColumns = ["id"], childColumns = ["projectId_current"])
    ],
    indices = [ Index("workspaceId_current"), Index("projectId_current") ]
)
data class DatabaseTask(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val serverId: Long?,
    @Embedded(prefix = "name_")
    val name: StringSyncProperty,
    @Embedded(prefix = "active_")
    val active: BooleanSyncProperty,
    @Embedded(prefix = "projectId_")
    val projectId: LongSyncProperty,
    @Embedded(prefix = "workspaceId_")
    val workspaceId: LongSyncProperty,
    @Embedded(prefix = "userId_")
    val userId: NullableLongSyncProperty
) {
    companion object {
        fun from(
            id: Long = 0,
            serverId: Long?,
            name: String,
            active: Boolean,
            projectId: Long,
            workspaceId: Long,
            userId: Long?
        ) = DatabaseTask(
            id,
            serverId,
            StringSyncProperty.from(name),
            BooleanSyncProperty.from(active),
            LongSyncProperty.from(projectId),
            LongSyncProperty.from(workspaceId),
            NullableLongSyncProperty.from(userId)
        )
    }
}
