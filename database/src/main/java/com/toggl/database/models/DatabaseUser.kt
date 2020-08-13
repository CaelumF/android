package com.toggl.database.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.toggl.database.properties.BooleanSyncProperty
import com.toggl.database.properties.IntSyncProperty
import com.toggl.database.properties.LongSyncProperty
import com.toggl.database.properties.StringSyncProperty

@Entity(
    tableName = "users",
    foreignKeys = [ ForeignKey(entity = DatabaseWorkspace::class, parentColumns = ["id"], childColumns = ["defaultWorkspaceId_current"]) ],
    indices = [Index("defaultWorkspaceId_current")]
)
data class DatabaseUser(
    @PrimaryKey
    val serverId: Long,
    val apiToken: String,
    @Embedded(prefix = "email_")
    val email: StringSyncProperty,
    @Embedded(prefix = "name_")
    val name: StringSyncProperty,
    @Embedded(prefix = "defaultWorkspaceId_")
    val defaultWorkspaceId: LongSyncProperty,

    // User Preferences
    val cellSwipeActionsEnabled: Boolean,
    val calendarIntegrationEnabled: Boolean,
    val calendarIds: List<String>,
    @Embedded(prefix = "manualModeEnabled_")
    val manualModeEnabled: BooleanSyncProperty,
    @Embedded(prefix = "twentyFourHourClockEnabled_")
    val twentyFourHourClockEnabled: BooleanSyncProperty,
    @Embedded(prefix = "groupSimilarTimeEntriesEnabled_")
    val groupSimilarTimeEntriesEnabled: BooleanSyncProperty,
    @Embedded(prefix = "dateFormat_")
    val dateFormat: StringSyncProperty,
    @Embedded(prefix = "durationFormat_")
    val durationFormat: StringSyncProperty,
    @Embedded(prefix = "firstDayOfTheWeek_")
    val firstDayOfTheWeek: IntSyncProperty,
    @Embedded(prefix = "smartAlertsOption_")
    val smartAlertsOption: StringSyncProperty
)
