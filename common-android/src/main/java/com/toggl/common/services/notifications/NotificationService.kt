package com.toggl.common.services.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.toggl.common.android.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class NotificationService @Inject constructor(@ApplicationContext private val context: Context, private val notificationManager: NotificationManager) {

    fun getNotificationBuilderForChannel(channel: TogglNotificationChannel): NotificationCompat.Builder {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel(channel)
        }
        return NotificationCompat.Builder(context, channel.id)
            .setColor(ContextCompat.getColor(context, R.color.primary))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel(channel: TogglNotificationChannel) {
        NotificationChannel(channel.id, channel.name(context), channel.importance()).apply {
            description = channel.description(context)
            notificationManager.createNotificationChannel(this)
        }
    }
}

enum class TogglNotificationChannel(val id: String) {
    Main("toggl-main")
}

@RequiresApi(Build.VERSION_CODES.O)
fun TogglNotificationChannel.importance() = when (this) {
    TogglNotificationChannel.Main -> NotificationManager.IMPORTANCE_DEFAULT
}

@RequiresApi(Build.VERSION_CODES.O)
fun TogglNotificationChannel.name(context: Context) = when (this) {
    TogglNotificationChannel.Main -> context.getString(R.string.app_name)
}

@RequiresApi(Build.VERSION_CODES.O)
fun TogglNotificationChannel.description(context: Context) = when (this) {
    TogglNotificationChannel.Main -> context.getString(R.string.app_name)
}
