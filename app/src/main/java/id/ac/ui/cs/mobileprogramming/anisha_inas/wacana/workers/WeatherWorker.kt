package id.ac.ui.cs.mobileprogramming.anisha_inas.wacana.workers

import android.app.Application
import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import id.ac.ui.cs.mobileprogramming.anisha_inas.wacana.Repository
import id.ac.ui.cs.mobileprogramming.anisha_inas.wacana.Repository.Companion.LATITUDE
import id.ac.ui.cs.mobileprogramming.anisha_inas.wacana.Repository.Companion.LONGITUDE

class WeatherWorker(
    val context: Context,
    val workerParams: WorkerParameters
) : Worker(context, workerParams) {
    override fun doWork(): Result {
        Repository.getInstance(context.applicationContext as Application)
            .getCurrentWeather(
                inputData.getString(LATITUDE) ?: "",
                inputData.getString(LONGITUDE) ?: ""
            )
        return Result.success()
    }
}