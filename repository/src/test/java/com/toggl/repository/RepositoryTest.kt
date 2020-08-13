package com.toggl.repository

import android.content.SharedPreferences
import com.toggl.api.ApiTokenProvider
import com.toggl.common.services.time.TimeService
import com.toggl.database.dao.ClientDao
import com.toggl.database.dao.ProjectDao
import com.toggl.database.dao.StartTimeEntryDatabaseResult
import com.toggl.database.dao.TagDao
import com.toggl.database.dao.TaskDao
import com.toggl.database.dao.TimeEntryDao
import com.toggl.database.dao.UserDao
import com.toggl.database.dao.WorkspaceDao
import com.toggl.database.models.DatabaseTimeEntry
import com.toggl.database.models.DatabaseTimeEntryWithTags
import com.toggl.database.properties.NullableDurationSyncProperty
import com.toggl.database.properties.OffsetDateTimeSyncProperty
import com.toggl.models.domain.TimeEntry
import com.toggl.models.domain.User
import com.toggl.models.validation.ApiToken
import com.toggl.models.validation.Email
import com.toggl.repository.dto.StartTimeEntryDTO
import com.toggl.repository.extensions.toDatabaseModel
import com.toggl.repository.extensions.toModelWithoutTags
import com.toggl.repository.interfaces.StartTimeEntryResult
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.mockk.called
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.OffsetDateTime

@DisplayName("Repository tests")
class RepositoryTest : CoroutineTest() {
    private val tagDao = mockk<TagDao>()
    private val projectDao = mockk<ProjectDao>()
    private val timeEntryDao = mockk<TimeEntryDao>()
    private val workspaceDao = mockk<WorkspaceDao>()
    private val clientDao = mockk<ClientDao>()
    private val taskDao = mockk<TaskDao>()
    private val userDao = mockk<UserDao>()
    private val sharedPreferences = mockk<SharedPreferences>()
    private val timeService = mockk<TimeService>()
    private var repository =
        Repository(projectDao, timeEntryDao, workspaceDao, clientDao, tagDao, taskDao, userDao, sharedPreferences, timeService)

    @Test
    fun `loadTimeEntries calls getAllTimeEntriesWithTags on the DAO`() = runBlockingTest {
        every { timeEntryDao.getAllTimeEntriesWithTags() } returns flowOf(emptyList())

        val loaded = repository.loadTimeEntries()

        verify(exactly = 1) { timeEntryDao.getAllTimeEntriesWithTags() }
        verify {
            workspaceDao wasNot called
            timeService wasNot called
            clientDao wasNot called
        }

        loaded.collect { timeEntries -> timeEntries.shouldBeEmpty() }
    }

    @Test
    fun `loadClients calls getAll on the DAO`() = runBlockingTest {
        every { clientDao.getAll() } returns flowOf(emptyList())

        val loaded = repository.loadClients()

        verify(exactly = 1) { clientDao.getAll() }
        verify {
            workspaceDao wasNot called
            timeService wasNot called
            timeEntryDao wasNot called
            projectDao wasNot called
        }
        loaded.collect { it.shouldBeEmpty() }
    }

    @Test
    fun `loadTags calls getAll on the DAO`() = runBlockingTest {
        every { tagDao.getAll() } returns flowOf(emptyList())

        val loaded = repository.loadTags()

        verify(exactly = 1) { tagDao.getAll() }
        verify {
            workspaceDao wasNot called
            timeService wasNot called
            timeEntryDao wasNot called
            projectDao wasNot called
        }
        loaded.collect { it.shouldBeEmpty() }
    }

    @Test
    fun `loadTasks calls getAll on the DAO`() = runBlockingTest {
        every { taskDao.getAll() } returns flowOf(emptyList())

        val loaded = repository.loadTasks()

        verify(exactly = 1) { taskDao.getAll() }
        verify {
            workspaceDao wasNot called
            timeService wasNot called
            timeEntryDao wasNot called
            projectDao wasNot called
        }
        loaded.collect { it.shouldBeEmpty() }
    }

    @Test
    fun `loadWorkspaces calls getAll on the DAO`() = runBlockingTest {
        every { workspaceDao.getAll() } returns flowOf(emptyList())

        repository.loadWorkspaces()

        verify(exactly = 1) { workspaceDao.getAll() }
        verify {
            timeEntryDao wasNot called
            timeService wasNot called
            clientDao wasNot called
        }
    }

    @Test
    fun `stopRunningTimeEntry updates all running time entries and returns the first one`() = runBlockingTest {
        val nowTime = OffsetDateTime.parse("2019-07-17T17:17:17+01:00")
        val timeEntryOneStartTime = OffsetDateTime.parse("2019-07-17T17:15:17+01:00")
        val timeEntryRunningOne =
            DatabaseTimeEntry.from(
                1,
                null,
                "one running",
                timeEntryOneStartTime,
                null,
                false,
                0,
                0,
                0,
                false
            )
        val timeEntryTwoStartTime = OffsetDateTime.parse("2019-07-17T12:17:17+01:00")
        val timeEntryRunningTwo = timeEntryRunningOne.copy(
            id = 3,
            startTime = OffsetDateTimeSyncProperty.from(timeEntryTwoStartTime)
        )
        val stoppedTimeEntries = listOf(timeEntryRunningOne, timeEntryRunningTwo).map {
            it.copy(
                duration = NullableDurationSyncProperty.from(
                    Duration.between(
                        timeEntryOneStartTime,
                        nowTime
                    )
                )
            )
        }
        val returnedTimeEntryWithTags = mockk<DatabaseTimeEntryWithTags>()
        every { timeService.now() } returns nowTime
        every { timeEntryDao.stopRunningTimeEntries(any()) } returns stoppedTimeEntries
        every { timeEntryDao.getOneTimeEntryWithTags(any()) } returns returnedTimeEntryWithTags
        every { returnedTimeEntryWithTags.timeEntry } returns timeEntryRunningOne
        every { returnedTimeEntryWithTags.tags } returns mockk()

        val result = repository.stopRunningTimeEntry()

        verify(exactly = 1) {
            timeEntryDao.stopRunningTimeEntries(nowTime)
            timeEntryDao.getOneTimeEntryWithTags(1)
        }
        verify {
            workspaceDao wasNot called
            clientDao wasNot called
        }
        result!!.id shouldBe stoppedTimeEntries.firstOrNull()!!.id
    }

    @Test
    fun `stopRunningTimeEntry doesn't update any time entries if none are running and doesn't return any time entries`() =
        runBlockingTest {
            val nowTime = OffsetDateTime.parse("2019-07-17T17:17:17+01:00")

            every { timeService.now() } returns nowTime
            every { timeEntryDao.stopRunningTimeEntries(any()) } returns listOf()

            val result = repository.stopRunningTimeEntry()

            verify(exactly = 1) { timeEntryDao.stopRunningTimeEntries(nowTime) }
            verify {
                workspaceDao wasNot called
                clientDao wasNot called
            }
            result shouldBe null
        }

    @Test
    fun `startTimeEntry stops currently running time entry and inserts a new one to DAO`() = runBlockingTest {
        val nowTime = OffsetDateTime.parse("2019-07-17T17:17:17+01:00")
        val timeEntryOneStartTime = OffsetDateTime.parse("2019-07-17T17:15:17+01:00")
        val timeEntryRunningOne = DatabaseTimeEntry.from(
            1,
            null,
            "Running",
            timeEntryOneStartTime,
            null,
            false,
            0,
            0,
            0,
            false
        )
        val timeEntryTwoStartTime = OffsetDateTime.parse("2019-07-17T12:17:17+01:00")
        val timeEntryRunningTwo = timeEntryRunningOne.copy(
            id = 3,
            startTime = OffsetDateTimeSyncProperty.from(timeEntryTwoStartTime)
        )
        val stoppedTimeEntries = listOf(timeEntryRunningOne, timeEntryRunningTwo).map {
            it.copy(
                duration = NullableDurationSyncProperty.from(
                    Duration.between(
                        timeEntryOneStartTime,
                        nowTime
                    )
                )
            )
        }
        val startedTimeEntry = DatabaseTimeEntry.from(
            5,
            null,
            "started",
            timeEntryOneStartTime,
            null,
            false,
            1,
            0,
            0,
            false
        )
        every { timeService.now() } returns nowTime
        every { timeEntryDao.startTimeEntry(any()) } returns StartTimeEntryDatabaseResult(
            DatabaseTimeEntryWithTags(
                startedTimeEntry,
                emptyList()
            ),
            stoppedTimeEntries
        )
        val startTimeEntryDTO = StartTimeEntryDTO(
            startedTimeEntry.description.current,
            startedTimeEntry.startTime.current,
            startedTimeEntry.billable.current,
            startedTimeEntry.workspaceId.current,
            startedTimeEntry.projectId.current,
            startedTimeEntry.taskId.current,
            listOf()
        )
        val result = repository.startTimeEntry(startTimeEntryDTO)

        verify(exactly = 1) {
            timeEntryDao.startTimeEntry(startTimeEntryDTO.toDatabaseModel())
        }
        verify {
            workspaceDao wasNot called
            clientDao wasNot called
        }
        result shouldBe StartTimeEntryResult(
            startedTimeEntry.toModelWithoutTags(),
            stoppedTimeEntries.firstOrNull()?.toModelWithoutTags()
        )
    }

    @Test
    fun `editTimeEntry updates the time entry and returns it`() = runBlockingTest {
        val nowTime = OffsetDateTime.parse("2019-07-17T17:17:17+01:00")
        val timeEntry = spyk(
            TimeEntry(1, "desc", nowTime, null, false, 1, null, null, false, emptyList())
        )
        every { timeEntryDao.updateTimeEntryWithTags(any()) } returns mockk()
        every { timeEntryDao.getOneTimeEntryWithTags(any()) } returns DatabaseTimeEntryWithTags(
            DatabaseTimeEntry.from(
                serverId = null,
                description = timeEntry.description,
                startTime = timeEntry.startTime,
                duration = timeEntry.duration,
                billable = timeEntry.billable,
                workspaceId = timeEntry.workspaceId,
                projectId = timeEntry.projectId,
                taskId = timeEntry.taskId,
                isDeleted = timeEntry.isDeleted
            ),
            listOf()
        )

        val result = repository.editTimeEntry(timeEntry)

        verify {
            workspaceDao wasNot called
            timeService wasNot called
            clientDao wasNot called
        }
        result shouldBe timeEntry
    }

    @Test
    fun `deleteTimeEntry sets isDeleted, updates the DAO and returns the updated entries`() = runBlockingTest {
        val timeEntry = TimeEntry(
            1337,
            "desc",
            OffsetDateTime.MAX,
            null,
            false,
            1,
            null,
            null,
            false,
            emptyList()
        )
        every { timeEntryDao.updateTimeEntryWithTags(any()) } returns mockk()
        every { timeEntryDao.getOneTimeEntryWithTags(any()) } returns DatabaseTimeEntryWithTags(
            DatabaseTimeEntry.from(
                id = timeEntry.id,
                serverId = null,
                description = timeEntry.description,
                startTime = timeEntry.startTime,
                duration = timeEntry.duration,
                billable = timeEntry.billable,
                workspaceId = timeEntry.workspaceId,
                projectId = timeEntry.projectId,
                taskId = timeEntry.taskId,
                isDeleted = timeEntry.isDeleted
            ),
            listOf()
        )

        val result =
            repository.deleteTimeEntry(timeEntry)

        verify(exactly = 1) {
            timeEntryDao.updateTimeEntryWithTags(any())
            timeEntryDao.getOneTimeEntryWithTags(any())
        }
        verify {
            workspaceDao wasNot called
            timeService wasNot called
            clientDao wasNot called
        }
        result shouldBe timeEntry.copy(isDeleted = true)
    }

    @Test
    fun `the set user method saves the api token in the shared preferences`() = runBlockingTest {

        val editor = mockk<SharedPreferences.Editor>()
        every { editor.apply() } returns Unit
        every { editor.putString(any(), any()) } returns editor
        every { sharedPreferences.edit() } returns editor
        every { workspaceDao.insert(any()) } returns 0L
        every { userDao.set(any()) } returns Unit

        val token = ApiToken.from("12345678901234567890123456789012") as ApiToken.Valid
        val user = User(
            id = 0,
            apiToken = token,
            defaultWorkspaceId = 1,
            email = Email.from("valid.mail@toggl.com") as Email.Valid,
            name = "name"
        )

        repository.set(user)

        verify {
            editor.putString(ApiTokenProvider.apiToken, token.toString())
        }
    }
}
