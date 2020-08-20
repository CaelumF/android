package com.toggl.sync.sequence

import java.lang.Exception

class SyncSequence(private val syncSteps: Collection<SyncStep>) {

    val stepCount = syncSteps.size
    private var syncRunning = false
    private var syncQueued = false
    private val lock = Any()

    internal suspend fun sync(listener: SyncSequenceListener) = sync(listOf(listener))

    internal suspend fun sync(listeners: List<SyncSequenceListener> = listOf()) {
        try {
            synchronized(lock) {
                syncQueued = true
                if (syncRunning) return
                syncRunning = true
            }

            var runCounter = 0
            while (shouldKeepSyncing()) {
                runSyncOnce(listeners, ++runCounter)
            }
        } catch (e: Exception) {
            // TODO
        } finally {
            synchronized(lock) {
                syncRunning = false
                syncQueued = false
            }
        }
    }

    internal fun queueSyncIfAlreadyRunning(): Boolean {
        synchronized(lock) {
            if (syncRunning) {
                syncQueued = true
            }
            return syncQueued
        }
    }

    private suspend fun runSyncOnce(listeners: List<SyncSequenceListener> = listOf(), runCounter: Int) {
        listeners.onSequenceRunStarted(runCounter)
        for ((index, step) in syncSteps.withIndex()) {
            listeners.onSequenceStepStarted(runCounter, index + 1, step)
            step.run()
            listeners.onSequenceStepCompleted(runCounter, index + 1, step)
        }
        listeners.onSequenceRunCompleted(runCounter)
    }

    private fun shouldKeepSyncing(): Boolean {
        synchronized(lock) {
            val wasQueued = syncQueued
            syncQueued = false
            return wasQueued
        }
    }
}

interface SyncStep {
    suspend fun run()
}

interface SyncSequenceListener {
    suspend fun onSequenceRunStarted(runCounter: Int) {}
    suspend fun onSequenceRunCompleted(runCounter: Int) {}
    suspend fun onSequenceStepStarted(runCounter: Int, stepCounter: Int, step: SyncStep) {}
    suspend fun onSequenceStepCompleted(runCounter: Int, stepCounter: Int, step: SyncStep) {}
}

private suspend fun List<SyncSequenceListener>.onSequenceRunStarted(runCounter: Int) =
    forEach { it.onSequenceRunStarted(runCounter) }

private suspend fun List<SyncSequenceListener>.onSequenceRunCompleted(runCounter: Int) =
    forEach { it.onSequenceRunCompleted(runCounter) }

private suspend fun List<SyncSequenceListener>.onSequenceStepStarted(runCounter: Int, stepCounter: Int, step: SyncStep) =
    forEach { it.onSequenceStepStarted(runCounter, stepCounter, step) }

private suspend fun List<SyncSequenceListener>.onSequenceStepCompleted(runCounter: Int, stepCounter: Int, step: SyncStep) =
    forEach { it.onSequenceStepCompleted(runCounter, stepCounter, step) }
