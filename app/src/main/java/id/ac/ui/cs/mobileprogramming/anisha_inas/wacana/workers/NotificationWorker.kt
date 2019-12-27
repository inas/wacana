package id.ac.ui.cs.mobileprogramming.anisha_inas.wacana.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_ONE_SHOT
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import id.ac.ui.cs.mobileprogramming.anisha_inas.wacana.R
import id.ac.ui.cs.mobileprogramming.anisha_inas.wacana.ui.home.HomeActivity


class NotificationWorker(val context: Context, params: WorkerParameters) : Worker(context, params) {


    companion object {
        const val WACANA_NOTIFICATION_CHANNEL = "WACANA_NOTIFICATION_CHANNEL"
        const val DESTINATION = "DESTINATION"
        const val EVENT_TIME = "EVENT_TIME"
        const val TRIP_NOTIFICATION = "TRIP_NOTIFICATION"
    }

    override fun doWork(): Result {
        val eventTime = inputData.getLong(EVENT_TIME, System.currentTimeMillis())
        val current = System.currentTimeMillis()
        if (eventTime > current) {
            createNotificationChannel()
            createNotification(buildPendingIntent())
            return Result.success()
        }

        return Result.failure()
    }


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(
                WACANA_NOTIFICATION_CHANNEL,
                WACANA_NOTIFICATION_CHANNEL,
                importance
            )

            val description =
                "A channel which shows notifications to remind user about upcoming trip"
            channel.description = description

            val notificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun buildPendingIntent(): PendingIntent {
        val intent = Intent(applicationContext, HomeActivity::class.java).apply {
            action = System.currentTimeMillis().toString()
        }
        return PendingIntent.getActivity(applicationContext, 1, intent, FLAG_ONE_SHOT)
    }

    private fun createNotification(intent: PendingIntent) {
        val destination = inputData.getString(DESTINATION)
        val notificationTitle =
            if (destination != null && destination.isNotEmpty()) context.getString(
                R.string.notification_title,
                destination
            ) else
                context.getString(R.string.notification_title_alternative)
        val notificationText = context.getString(R.string.notification_body)

        val notificationBuilder =
            NotificationCompat.Builder(applicationContext, WACANA_NOTIFICATION_CHANNEL)
                .setSmallIcon(R.drawable.ic_notification_24dp)
                .setContentTitle(notificationTitle)
                .setContentText(notificationText)
                .setContentIntent(intent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationManager = NotificationManagerCompat.from(applicationContext)

        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }
}
