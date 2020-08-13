package com.toggl.settings.domain

import com.toggl.architecture.DispatcherProvider
import com.toggl.architecture.core.Effect
import kotlinx.coroutines.withContext

class InsertMockDataEffect(
    private val dataSetSize: Int,
    private val mockDatabaseInitializer: MockDatabaseInitializer,
    private val dispatcherProvider: DispatcherProvider
) : Effect<SettingsAction.FinishedEditingSetting> {

    override suspend fun execute(): SettingsAction.FinishedEditingSetting =
        withContext(dispatcherProvider.io) {
            mockDatabaseInitializer.init(dataSetSize)
            SettingsAction.FinishedEditingSetting
        }
}
