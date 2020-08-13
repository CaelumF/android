package com.toggl.database.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.toggl.database.properties.BooleanSyncProperty
import com.toggl.database.properties.LongSyncProperty
import com.toggl.database.properties.NullableBooleanSyncProperty
import com.toggl.database.properties.NullableLongSyncProperty
import com.toggl.database.properties.StringSyncProperty

@Entity(
    tableName = "projects",
    foreignKeys = [
        ForeignKey(entity = DatabaseWorkspace::class, parentColumns = ["id"], childColumns = ["workspaceId_current"]),
        ForeignKey(entity = DatabaseClient::class, parentColumns = ["id"], childColumns = ["clientId_current"])
    ],
    indices = [ Index("workspaceId_current"), Index("clientId_current") ]
)
data class DatabaseProject(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val serverId: Long?,
    @Embedded(prefix = "name_")
    val name: StringSyncProperty,
    @Embedded(prefix = "color_")
    val color: StringSyncProperty,
    @Embedded(prefix = "active_")
    val active: BooleanSyncProperty,
    @Embedded(prefix = "isPrivate_")
    val isPrivate: BooleanSyncProperty,
    @Embedded(prefix = "billable_")
    val billable: NullableBooleanSyncProperty,
    @Embedded(prefix = "workspaceId_")
    val workspaceId: LongSyncProperty,
    @Embedded(prefix = "clientId_")
    val clientId: NullableLongSyncProperty
) {
    companion object {
        fun from(
            id: Long = 0,
            serverId: Long?,
            name: String,
            color: String,
            active: Boolean,
            isPrivate: Boolean,
            billable: Boolean?,
            workspaceId: Long,
            clientId: Long?
        ) = DatabaseProject(
            id,
            serverId,
            StringSyncProperty.from(name),
            StringSyncProperty.from(color),
            BooleanSyncProperty.from(active),
            BooleanSyncProperty.from(isPrivate),
            NullableBooleanSyncProperty.from(billable),
            LongSyncProperty.from(workspaceId),
            NullableLongSyncProperty.from(clientId)
        )
    }
}
