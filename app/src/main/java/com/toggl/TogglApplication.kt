package com.toggl

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.toggl.architecture.DispatcherProvider
import com.toggl.architecture.StoreScopeProvider
import com.toggl.architecture.core.Store
import com.toggl.domain.AppAction
import com.toggl.domain.AppState
import com.toggl.domain.loading.LoadingAction
import com.toggl.initializers.AppInitializers
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@HiltAndroidApp
class TogglApplication : Application(), CoroutineScope, StoreScopeProvider, Configuration.Provider {

    @Inject lateinit var appInitializers: AppInitializers
    @Inject lateinit var dispatchersProviders: DispatcherProvider
    @Inject lateinit var store: Store<AppState, AppAction>
    @Inject lateinit var workerFactory: HiltWorkerFactory

    override val coroutineContext: CoroutineContext by lazy {
        dispatchersProviders.main
    }

    override fun onCreate() {
        super.onCreate()
        appInitializers.initialize(this)
        store.dispatch(AppAction.Loading(LoadingAction.StartLoading))
    }

    override fun getStoreScope(): CoroutineScope =
        this

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}
