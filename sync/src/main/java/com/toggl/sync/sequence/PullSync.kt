package com.toggl.sync.sequence

import kotlinx.coroutines.delay
import javax.inject.Inject

class PullSync @Inject constructor() : SyncStep {
    override suspend fun run() {
        delay(2000)
    }
}
