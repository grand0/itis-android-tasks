package ru.kpfu.itis.ponomarev.androidcourse.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import ru.kpfu.itis.ponomarev.androidcourse.MainActivity
import ru.kpfu.itis.ponomarev.androidcourse.R

object NotificationsUtil {

    private fun getNotificationChannelId(context: Context, importance: Importance) =
        context.getString(R.string.default_notification_channel_id) + "_" + importance.name

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context
                .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            for (importance in Importance.values()) {
                NotificationChannel(
                    getNotificationChannelId(context, importance),
                    importance.text,
                    importance.id,
                ).also {
                    notificationManager.createNotificationChannel(it)
                }
            }
        }
    }

    fun sendNotification(context: Context, title: String? = null, text: String? = null) {
        val channelId = getNotificationChannelId(context, NotificationSettings.importance)
        val builder = NotificationCompat.Builder(
            context,
            channelId,
        )
            .setSmallIcon(R.drawable.baseline_notifications_24)
            .setContentTitle(title)
            .setContentText(text)
            .setVisibility(NotificationSettings.visibility.id)
            .setPublicVersion(
                NotificationCompat.Builder(
                    context,
                    channelId
                )
                    .setSmallIcon(R.drawable.baseline_notifications_24)
                    .setContentTitle(title)
                    .build()
            )

        if (NotificationSettings.isBigText && (text?.length ?: 0) > 20) {
            builder.setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(text)
            )
        }

        if (NotificationSettings.showActions) {
            val showMessageIntent = Intent(context, MainActivity::class.java)
            showMessageIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            showMessageIntent.putExtra("action", MainActivity.ACTION_SHOW_MESSAGE)
            val pShowMessageIntent = PendingIntent.getActivity(
                context,
                0,
                showMessageIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
            builder.addAction(
                NotificationCompat.Action(
                    null,
                    "Show message",
                    pShowMessageIntent
                )
            )

            val showSettingsIntent = Intent(context, MainActivity::class.java)
            showSettingsIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            showSettingsIntent.putExtra("action", MainActivity.ACTION_SHOW_SETTINGS)
            val pShowSettingsIntent = PendingIntent.getActivity(
                context,
                1,
                showSettingsIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
            builder.addAction(
                NotificationCompat.Action(
                    null,
                    "Show settings",
                    pShowSettingsIntent
                )
            )
        }

        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pIntent = PendingIntent.getActivity(
            context,
            2,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        builder.setContentIntent(pIntent)

        val notificationManager = context
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, builder.build())
    }
}
