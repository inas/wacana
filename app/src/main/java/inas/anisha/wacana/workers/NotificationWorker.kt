package inas.anisha.wacana.workers

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
import inas.anisha.wacana.R
import inas.anisha.wacana.ui.home.HomeActivity
import inas.anisha.wacana.ui.newTrip.NewTripActivity.Companion.DESTINATION


class NotificationWorker(context: Context, params: WorkerParameters) : Worker(context, params) {


    companion object {
        const val WACANA_NOTIFICATION_CHANNEL = "WACANA_NOTIFICATION_CHANNEL"
    }

    override fun doWork(): Result {
        // Method to trigger an instant notification
        createNotificationChannel()
        createNotification(buildPendingIntent())

        return Result.success()
        // (Returning RETRY tells WorkManager to try this task again
        // later; FAILURE says not to try again.)
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

            // Register the channel with the system
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
        //get latest event details
        val destination = inputData.getString(DESTINATION)
        val notificationTitle =
            if (destination != null && destination.isNotEmpty()) "Get ready for your trip to " + destination + "!" else
                "Get ready for your trip!"
        val notificationText = "Start packing now"

        //build the notification
        val notificationBuilder =
            NotificationCompat.Builder(applicationContext, WACANA_NOTIFICATION_CHANNEL)
                .setSmallIcon(R.drawable.ic_notification_24dp)
                .setContentTitle(notificationTitle)
                .setContentText(notificationText)
                .setContentIntent(intent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        //trigger the notification
        val notificationManager = NotificationManagerCompat.from(applicationContext)

        //we give each notification the ID of the event it's describing,
        //to ensure they all show up and there are no duplicates
        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }
}
