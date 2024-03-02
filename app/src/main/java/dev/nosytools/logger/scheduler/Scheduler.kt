package dev.nosytools.logger.scheduler

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import nosy_logger.LoggerOuterClass.Log
import java.util.concurrent.TimeUnit

internal class Scheduler(private val context: Context, private val apiKey: String) {

    private val workManager by lazy { WorkManager.getInstance(context) }

    private val constraints by lazy {
        Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .setRequiresStorageNotLow(true)
            .build()
    }
    private val data by lazy { SendLogsWorker.Arguments(apiKey).serialize() }

    fun schedule(log: Log) {
        SharedBuffer.push(log)

        val request = PeriodicWorkRequestBuilder<SendLogsWorker>(15, TimeUnit.MINUTES)
            .setInitialDelay(10, TimeUnit.SECONDS)
            .setConstraints(constraints)
            .setInputData(data)
            .build()

        workManager.enqueueUniquePeriodicWork(WORK_NAME, ExistingPeriodicWorkPolicy.UPDATE, request)
    }

    private companion object {
        private const val WORK_NAME = "nosy-logger-send-logs"
    }
}