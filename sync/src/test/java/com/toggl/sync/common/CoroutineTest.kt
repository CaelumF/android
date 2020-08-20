package com.toggl.sync.common

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

abstract class CoroutineTest {
    private val testDispatcher = TestCoroutineDispatcher()

    @BeforeEach
    open fun beforeTest() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterEach
    open fun afterTest() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }
}
