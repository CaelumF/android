package com.toggl.database.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.toggl.database.properties.BooleanSyncProperty
import com.toggl.database.properties.LongSyncProperty
import com.toggl.database.properties.NullableDurationSyncProperty
import com.toggl.database.properties.NullableLongSyncProperty
import com.toggl.database.properties.OffsetDateTimeSyncProperty
import com.toggl.database.properties.StringSyncProperty
import java.time.Duration
import java.time.OffsetDateTime

@Entity(
    tableName = "time_entries",
    foreignKeys = [
        ForeignKey(entity = DatabaseProject::class, parentColumns = ["id"], childColumns = ["projectId_current"]),
        ForeignKey(entity = DatabaseWorkspace::class, parentColumns = ["id"], childColumns = ["workspaceId_current"]),
        ForeignKey(entity = DatabaseTask::class, parentColumns = ["id"], childColumns = ["taskId_current"])
    ],
    indices = [Index("projectId_current"), Index("workspaceId_current"), Index("taskId_current")]
)
data class DatabaseTimeEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val serverId: Long?,
    @Embedded(prefix = "description_")
    val description: StringSyncProperty,
    @Embedded(prefix = "startTime_")
    val startTime: OffsetDateTimeSyncProperty,
    @Embedded(prefix = "duration_")
    val duration: NullableDurationSyncProperty,
    @Embedded(prefix = "billable_")
    val billable: BooleanSyncProperty,
    @Embedded(prefix = "workspaceId_")
    val workspaceId: LongSyncProperty,
    @Embedded(prefix = "projectId_")
    val projectId: NullableLongSyncProperty,
    @Embedded(prefix = "taskId_")
    val taskId: NullableLongSyncProperty,
    @Embedded(prefix = "isDeleted_")
    val isDeleted: BooleanSyncProperty
) {
    companion object {
        fun from(
            id: Long = 0,
            serverId: Long?,
            description: String,
            startTime: OffsetDateTime,
            duration: Duration?,
            billable: Boolean,
            workspaceId: Long,
            projectId: Long?,
            taskId: Long?,
            isDeleted: Boolean
        ) = DatabaseTimeEntry(
            id,
            serverId,
            StringSyncProperty.from(description),
            OffsetDateTimeSyncProperty.from(startTime),
            NullableDurationSyncProperty.from(duration),
            BooleanSyncProperty.from(billable),
            LongSyncProperty.from(workspaceId),
            NullableLongSyncProperty.from(projectId),
            NullableLongSyncProperty.from(taskId),
            BooleanSyncProperty.from(isDeleted)
        )
    }
}
