package com.toggl.common.extensions

import android.content.Intent
import android.net.Uri
import android.text.Spannable
import android.text.style.ClickableSpan
import android.view.View

fun Spannable.setLinkSpan(linkToOpen: Uri, linkText: String) =
    this.indexOf(linkText).let {
        this.setSpan(
            LinkClickableSpan(linkToOpen),
            it,
            it + linkText.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

private class LinkClickableSpan(val urlToOpen: Uri) : ClickableSpan() {
    override fun onClick(widget: View) {
        val intent = Intent(Intent.ACTION_VIEW, urlToOpen)
        if (intent.resolveActivity(widget.context.packageManager) != null) {
            widget.context.startActivity(intent)
        }
    }
}
