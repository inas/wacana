package inas.anisha.wacana.apiProvider.retrofit

import android.app.Application
import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import inas.anisha.wacana.Repository
import inas.anisha.wacana.Repository.Companion.LATITUDE
import inas.anisha.wacana.Repository.Companion.LONGITUDE

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