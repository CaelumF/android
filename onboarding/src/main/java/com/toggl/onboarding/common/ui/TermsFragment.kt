package com.toggl.onboarding.common.ui

import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ImageSpan
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.toggl.common.extensions.scaleToLineHeight
import com.toggl.common.extensions.setLinkSpan
import com.toggl.onboarding.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_terms.*

@AndroidEntryPoint
class TermsFragment : Fragment(R.layout.fragment_terms) {
    private val privacyPolicyUrl = Uri.parse("https://toggl.com/legal/privacy/")
    private val termsOfServiceUrl = Uri.parse("https://toggl.com/legal/terms/")

    private val store: TermsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prepareHeaderText()
        prepareBodyText()
    }

    private fun prepareHeaderText() {
        val logoImageSpan = requireContext().let {
            val logoDrawable = ContextCompat.getDrawable(it, R.drawable.ic_toggl_logo)!!
            logoDrawable.setTint(ContextCompat.getColor(it, R.color.authentication_text))
            logoDrawable.scaleToLineHeight(terms_header.lineHeight)
            ImageSpan(logoDrawable)
        }

        val headerBuilder = SpannableStringBuilder(getString(R.string.terms_and_conditions_header))
        val newlineIndex = headerBuilder.indexOf('\n')
        headerBuilder.setSpan(logoImageSpan, newlineIndex - 1, newlineIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        terms_header.text = headerBuilder
    }

    private fun prepareBodyText() {
        val spannableTerms = requireContext().let {
            val termsAndConditions = it.getString(R.string.terms_of_service)
            val privacyPolicy = it.getString(R.string.privacy_policy)
            val getStarted = it.getString(R.string.get_started)
            val termsText =
                it.getString(R.string.terms_and_conditions_content, getStarted, termsAndConditions, privacyPolicy)

            SpannableString(termsText).apply {
                setLinkSpan(termsOfServiceUrl, termsAndConditions)
                setLinkSpan(privacyPolicyUrl, privacyPolicy)
            }
        }

        terms_text.apply {
            text = spannableTerms
            movementMethod = LinkMovementMethod.getInstance()
        }
    }
}
