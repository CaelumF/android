package com.toggl.database.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.toggl.database.properties.LongSyncProperty
import com.toggl.database.properties.StringSyncProperty

@Entity(
    tableName = "tags",
    foreignKeys = [
        ForeignKey(entity = DatabaseWorkspace::class, parentColumns = ["id"], childColumns = ["workspaceId_current"])
    ],
    indices = [ Index("workspaceId_current") ]
)
data class DatabaseTag(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val serverId: Long?,
    @Embedded(prefix = "name_")
    val name: StringSyncProperty,
    @Embedded(prefix = "workspaceId_")
    val workspaceId: LongSyncProperty
) {
    companion object {
        fun from(
            id: Long = 0,
            serverId: Long?,
            name: String,
            workspaceId: Long
        ) = DatabaseTag(
            id,
            serverId,
            StringSyncProperty.from(name),
            LongSyncProperty.from(workspaceId)
        )
    }
}
