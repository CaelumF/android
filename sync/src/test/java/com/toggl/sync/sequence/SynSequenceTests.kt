package com.toggl.sync.sequence

import com.toggl.sync.common.CoroutineTest
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.ints.shouldBeExactly
import io.mockk.coVerifyOrder
import io.mockk.mockk
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("The SynSequence")
class SynSequenceTests : CoroutineTest() {
    private val step1 = mockk<SyncStep>(relaxUnitFun = true)
    private val step2 = mockk<SyncStep>(relaxUnitFun = true)
    private val steps: List<SyncStep> = listOf(step1, step2)
    private val syncSequence = SyncSequence(steps)

    @Test
    fun `runs steps in the right order`() = runBlockingTest {
        val listener = getTestListener()
        syncSequence.sync(listener)
        coVerifyOrder {
            step1.run()
            step2.run()
        }
        listener.startedRuns shouldBeExactly 1
    }

    @Test
    fun `calls all listeners correctly`() = runBlockingTest {
        val listener1 = mockk<SyncSequenceListener>(relaxUnitFun = true)
        val listener2 = mockk<SyncSequenceListener>(relaxUnitFun = true)
        val listeners = listOf(listener1, listener2)

        syncSequence.sync(listeners)

        coVerifyOrder {
            listener1.onSequenceRunStarted(1)
            listener2.onSequenceRunStarted(1)

            listener1.onSequenceStepStarted(1, 1, step1)
            listener2.onSequenceStepStarted(1, 1, step1)

            listener1.onSequenceStepCompleted(1, 1, step1)
            listener2.onSequenceStepCompleted(1, 1, step1)

            listener1.onSequenceStepStarted(1, 2, step2)
            listener2.onSequenceStepStarted(1, 2, step2)

            listener1.onSequenceStepCompleted(1, 2, step2)
            listener2.onSequenceStepCompleted(1, 2, step2)

            listener1.onSequenceRunCompleted(1)
            listener2.onSequenceRunCompleted(1)
        }
    }

    @Test
    fun `won't queue sync if it's not already running`() = runBlockingTest {
        syncSequence.queueSyncIfAlreadyRunning().shouldBeFalse()
    }

    @Test
    fun `queues new sync if it's already running`() = runBlockingTest {
        val listener = getTestListener(
            doBeforeRun = listOf(
                ActionBeforeStep(action = { syncSequence.queueSyncIfAlreadyRunning().shouldBeTrue() }, step = 1)
            )
        )
        syncSequence.sync(listener)
        listener.startedRuns shouldBeExactly 2
    }

    @Test
    fun `queues no more than one sync`() = runBlockingTest {
        val listener = getTestListener(
            doBeforeRun = listOf(
                ActionBeforeStep(
                    action = {
                        syncSequence.queueSyncIfAlreadyRunning()
                        syncSequence.queueSyncIfAlreadyRunning()
                        syncSequence.queueSyncIfAlreadyRunning()
                        syncSequence.queueSyncIfAlreadyRunning()
                    },
                    step = 1
                )
            )
        )
        syncSequence.sync(listener)
        listener.startedRuns shouldBeExactly 2
    }

    private fun getTestListener(
        onSequenceRunStarted: (Int) -> Unit = {},
        onSequenceRunCompleted: (Int) -> Unit = {},
        onSequenceStepStarted: (Int, Int, SyncStep) -> Unit = { _: Int, _: Int, _: SyncStep -> },
        onSequenceStepCompleted: (Int, Int, SyncStep) -> Unit = { _: Int, _: Int, _: SyncStep -> },
        doBeforeRun: List<ActionBeforeStep> = emptyList()
    ) = object : SyncSequenceListener {

        var startedRuns = 0

        override suspend fun onSequenceRunStarted(runCounter: Int) {
            onSequenceRunStarted(runCounter)
            doBeforeRun.forEach { actionBeforeStep ->
                if (runCounter == actionBeforeStep.step) actionBeforeStep.action()
            }
            startedRuns = runCounter
        }

        override suspend fun onSequenceRunCompleted(runCounter: Int) {
            onSequenceRunCompleted(runCounter)
        }

        override suspend fun onSequenceStepStarted(runCounter: Int, stepCounter: Int, step: SyncStep) {
            onSequenceStepStarted(runCounter, stepCounter, step)
        }

        override suspend fun onSequenceStepCompleted(runCounter: Int, stepCounter: Int, step: SyncStep) {
            onSequenceStepCompleted(runCounter, stepCounter, step)
        }
    }

    data class ActionBeforeStep(val action: () -> Unit, val step: Int)
}
