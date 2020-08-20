package com.toggl.settings.ui.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.toggl.architecture.extensions.select
import com.toggl.common.feature.compose.extensions.createComposeView
import com.toggl.settings.di.ProvideAboutSettingsSelector
import com.toggl.settings.domain.SettingsSelector
import com.toggl.settings.ui.SettingsStoreViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AboutFragment : Fragment() {

    @Inject @JvmField @ProvideAboutSettingsSelector var settingsSelector: SettingsSelector? = null // https://github.com/google/dagger/issues/1883#issuecomment-642565920 🤷‍

    private val store: SettingsStoreViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = createComposeView { statusBarHeight, navigationBarHeight ->
        val selectedState = store.state
            .select(settingsSelector!!)
        AboutPage(selectedState, statusBarHeight, navigationBarHeight, store::dispatch)
    }
}
