package com.toggl.sync.di

import com.toggl.sync.sequence.CleanUp
import com.toggl.sync.sequence.PullSync
import com.toggl.sync.sequence.PushSync
import com.toggl.sync.sequence.ResolveOutstandingPushRequest
import com.toggl.sync.sequence.SyncSequence
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object SyncModule {
    @Provides
    @Singleton
    fun syncSequence(
        pullSync: PullSync,
        pushSync: PushSync,
        resolveOutstandingPushRequest: ResolveOutstandingPushRequest,
        cleanUp: CleanUp,
    ): SyncSequence = SyncSequence(
        listOf(
            resolveOutstandingPushRequest,
            pullSync,
            pushSync,
            resolveOutstandingPushRequest,
            cleanUp
        )
    )
}
