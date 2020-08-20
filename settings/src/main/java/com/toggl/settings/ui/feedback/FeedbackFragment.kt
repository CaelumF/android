package com.toggl.settings.ui.feedback

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.toggl.architecture.Loadable
import com.toggl.common.feature.compose.extensions.createComposeView
import com.toggl.settings.R
import com.toggl.settings.domain.SettingsAction
import com.toggl.settings.ui.SettingsStoreViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class FeedbackFragment : Fragment() {

    private val store: SettingsStoreViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = createComposeView { statusBarHeight, navigationBarHeight ->
        val sendFeedbackRequest = store.state.map { it.localState.sendFeedbackRequest }.distinctUntilChanged()

        FeedbackPage(
            sendFeedbackRequest = sendFeedbackRequest,
            statusBarHeight = statusBarHeight,
            navigationBarHeight = navigationBarHeight,
            dispatcher = store::dispatch
        )

        sendFeedbackRequest
            .onEach { request ->
                val msg = when (request) {
                    is Loadable.Loaded -> getString(R.string.feedback_thank_you)
                    is Loadable.Error -> request.failure.errorMessage
                    else -> return@onEach
                }
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                store.dispatch(SettingsAction.SendFeedbackResultSeen)
            }
            .launchIn(lifecycleScope)
    }
}
