package com.toggl.settings.domain

import android.content.Context
import com.squareup.moshi.Moshi
import com.toggl.api.models.ApiClient
import com.toggl.api.models.ApiProject
import com.toggl.api.models.ApiTag
import com.toggl.api.models.ApiTask
import com.toggl.api.models.ApiTimeEntry
import com.toggl.api.models.ApiUser
import com.toggl.api.models.ApiWorkspace
import com.toggl.api.network.models.sync.PullResponse
import com.toggl.api.network.models.sync.PullResponseJsonAdapter
import com.toggl.database.dao.ClientDao
import com.toggl.database.dao.ProjectDao
import com.toggl.database.dao.TagDao
import com.toggl.database.dao.TaskDao
import com.toggl.database.dao.TimeEntryDao
import com.toggl.database.dao.UserDao
import com.toggl.database.dao.WorkspaceDao
import com.toggl.database.models.DatabaseClient
import com.toggl.database.models.DatabaseProject
import com.toggl.database.models.DatabaseTag
import com.toggl.database.models.DatabaseTask
import com.toggl.database.models.DatabaseTimeEntry
import com.toggl.database.models.DatabaseUser
import com.toggl.database.models.DatabaseWorkspace
import com.toggl.database.properties.BooleanSyncProperty
import com.toggl.database.properties.IntSyncProperty
import com.toggl.database.properties.LongSyncProperty
import com.toggl.database.properties.StringSyncProperty
import com.toggl.models.domain.UserPreferences
import com.toggl.models.domain.WorkspaceFeature
import com.toggl.settings.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import java.nio.charset.Charset
import java.time.Duration
import javax.inject.Inject

@Suppress("BlockingMethodInNonBlockingContext")
class MockDatabaseInitializer @Inject constructor(
    @ApplicationContext private val context: Context,
    private val moshi: Moshi,
    private val projectDao: ProjectDao,
    private val timeEntryDao: TimeEntryDao,
    private val workspaceDao: WorkspaceDao,
    private val clientDao: ClientDao,
    private val tagDao: TagDao,
    private val taskDao: TaskDao,
    private val userDao: UserDao
) {
    // 99.9 = 2760
    // 99.5 = 1140
    // 99.0 = 866
    // 90.0 = 274
    // 80.0 = 162
    // median = 67
    suspend fun init(numberOfTe: Int = 866) {
        if (!BuildConfig.DEBUG) return

        timeEntryDao.clear()
        taskDao.clear()
        projectDao.clear()
        tagDao.clear()
        clientDao.clear()
        userDao.clear()
        workspaceDao.clear()

        if (numberOfTe == 0) return

        val pullResponseJson = parseFile("pull_response.json")

        val pullResponse: PullResponse = PullResponseJsonAdapter(moshi).fromJson(pullResponseJson)!!

        workspaceDao.insertAll(pullResponse.workspaces.map { it.toDatabaseModel() })
        userDao.set(pullResponse.user.toDatabaseModel())
        clientDao.insertAll(pullResponse.clients.map { it.toDatabaseModel() })
        projectDao.insertAll(pullResponse.projects.map { it.toDatabaseModel() })
        tagDao.insertAll(pullResponse.tags.map { it.toDatabaseModel() })
        taskDao.insertAll(pullResponse.tasks.map { it.toDatabaseModel() })
        timeEntryDao.insertAllTimeEntries(pullResponse.timeEntries.take(numberOfTe).map { it.toDatabaseModel() })
    }

    private suspend fun parseFile(filename: String): String {
        return try {
            val file = context.assets.open(filename)
            val size: Int = file.available()
            val buffer = ByteArray(size)
            file.read(buffer)
            file.close()
            String(buffer, Charset.defaultCharset())
        } catch (ex: IOException) {
            throw IllegalStateException("Mock data parsing fail")
        }
    }

    private fun ApiTimeEntry.toDatabaseModel() = DatabaseTimeEntry.from(
        id = id,
        serverId = id,
        description = description,
        startTime = start,
        duration = if (duration >= 0) Duration.ofSeconds(duration) else null,
        billable = billable,
        workspaceId = workspaceId,
        projectId = projectId,
        taskId = taskId,
        isDeleted = serverDeletedAt != null
    )

    private fun ApiClient.toDatabaseModel() = DatabaseClient.from(
        id = id,
        serverId = id,
        name = name,
        workspaceId = workspaceId
    )

    private fun ApiProject.toDatabaseModel() = DatabaseProject.from(
        id = id,
        serverId = id,
        name = name,
        color = color,
        active = active,
        isPrivate = isPrivate,
        billable = billable,
        workspaceId = workspaceId,
        clientId = clientId
    )

    private fun ApiTask.toDatabaseModel() = DatabaseTask.from(
        id = id,
        serverId = id,
        name = name,
        active = active,
        projectId = projectId,
        workspaceId = workspaceId,
        userId = userId
    )

    private fun ApiTag.toDatabaseModel() = DatabaseTag(
        id = id,
        serverId = id,
        name = name,
        workspaceId = workspaceId
    )

    private fun ApiWorkspace.toDatabaseModel() = DatabaseWorkspace(
        id = id,
        serverId = id,
        name = name,
        features = listOf(WorkspaceFeature.Pro)
    )

    private fun ApiUser.toDatabaseModel() = DatabaseUser(
        serverId = id ?: userId ?: throw IllegalStateException(),
        name = StringSyncProperty.from(fullname),
        email = StringSyncProperty.from(email),
        apiToken = apiToken,
        defaultWorkspaceId = LongSyncProperty.from(defaultWorkspaceId ?: 0),

        // User Preferences
        manualModeEnabled = BooleanSyncProperty.from(UserPreferences.default.manualModeEnabled),
        twentyFourHourClockEnabled = BooleanSyncProperty.from(UserPreferences.default.twentyFourHourClockEnabled),
        groupSimilarTimeEntriesEnabled = BooleanSyncProperty.from(UserPreferences.default.groupSimilarTimeEntriesEnabled),
        cellSwipeActionsEnabled = UserPreferences.default.cellSwipeActionsEnabled,
        calendarIntegrationEnabled = UserPreferences.default.calendarIntegrationEnabled,
        calendarIds = UserPreferences.default.calendarIds,
        dateFormat = StringSyncProperty.from(UserPreferences.default.dateFormat.name),
        durationFormat = StringSyncProperty.from(UserPreferences.default.durationFormat.name),
        firstDayOfTheWeek = IntSyncProperty.from(UserPreferences.default.firstDayOfTheWeek.value),
        smartAlertsOption = StringSyncProperty.from(UserPreferences.default.smartAlertsOption.name)
    )
}
