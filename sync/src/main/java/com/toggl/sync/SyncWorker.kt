package com.toggl.sync

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.toggl.common.services.notifications.NotificationService
import com.toggl.common.services.notifications.TogglNotificationChannel
import com.toggl.sync.sequence.SyncSequence
import com.toggl.sync.sequence.SyncSequenceListener
import com.toggl.sync.sequence.SyncStep

class SyncWorker @WorkerInject constructor(
    @Assisted context: Context,
    @Assisted parameters: WorkerParameters,
    private val syncSequence: SyncSequence,
    private val notificationService: NotificationService
) : CoroutineWorker(context, parameters) {

    private val debugSyncListener = object : SyncSequenceListener {
        override suspend fun onSequenceStepStarted(runCounter: Int, stepCounter: Int, step: SyncStep) {
            setForeground(createForegroundInfo("Run: $runCounter Step: $stepCounter / ${syncSequence.stepCount}"))
        }

        override suspend fun onSequenceRunStarted(runCounter: Int) {
            setForeground(createForegroundInfo("Run $runCounter started"))
        }
    }

    override suspend fun doWork(): Result {
        return try {
            syncSequence.sync(debugSyncListener)
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private fun createForegroundInfo(progress: String): ForegroundInfo {
        val title = "Syncing"
        val notification = notificationService.getNotificationBuilderForChannel(TogglNotificationChannel.Main)
            .setContentTitle(title)
            .setTicker(title)
            .setContentText(progress)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setOngoing(true)
            .build()
        return ForegroundInfo(foregroundInfoNotificationId, notification)
    }

    companion object {
        const val foregroundInfoNotificationId = 489
    }
}
