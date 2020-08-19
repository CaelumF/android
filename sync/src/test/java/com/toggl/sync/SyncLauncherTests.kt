package com.toggl.sync

import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.toggl.sync.common.CoroutineTest
import com.toggl.sync.sequence.SyncSequence
import io.mockk.Called
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("The SyncLauncher")
class SyncLauncherTests : CoroutineTest() {
    private val syncSequence = mockk<SyncSequence>(relaxed = true)
    private val workManager = mockk<WorkManager>(relaxed = true)
    private val syncLauncher = SyncLauncher(syncSequence, workManager)

    @Test
    fun `enqueues new sync work if it's not running already`() = runBlockingTest {
        coEvery { syncSequence.queueSyncIfAlreadyRunning() } returns false
        syncLauncher.enqueueSync()
        verify { syncSequence.queueSyncIfAlreadyRunning() }
        verify { workManager.beginUniqueWork(any(), any(), any<OneTimeWorkRequest>()) }
    }

    @Test
    fun `runs steps in the right order`() = runBlockingTest {
        coEvery { syncSequence.queueSyncIfAlreadyRunning() } returns true
        syncLauncher.enqueueSync()
        verify { syncSequence.queueSyncIfAlreadyRunning() }
        verify { workManager wasNot Called }
    }
}
