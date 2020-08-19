package com.toggl.sync

import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.toggl.sync.sequence.SyncSequence
import javax.inject.Inject

class SyncLauncher @Inject constructor(private val syncSequence: SyncSequence, private val workManager: WorkManager) {
    fun enqueueSync() {
        val wasQueued = syncSequence.queueSyncIfAlreadyRunning()
        if (wasQueued) return

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncWork = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .build()

        workManager.beginUniqueWork(
            syncWorkName,
            ExistingWorkPolicy.KEEP,
            syncWork
        ).enqueue()
    }

    fun freezeSync() {
        workManager.cancelUniqueWork(syncWorkName)
    }

    companion object {
        const val syncWorkName = "toggl-sync-work"
    }
}
