package dev.nosytools.logger.scheduler

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dev.nosytools.logger.TemporaryLog
import java.util.concurrent.TimeUnit

internal class Scheduler(private val context: Context) {

    private val workManager by lazy { WorkManager.getInstance(context) }

    private val constraints by lazy {
        Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .setRequiresStorageNotLow(true)
            .build()
    }

    fun schedule(log: TemporaryLog) {
        SharedBuffer.push(log)

        val request = PeriodicWorkRequestBuilder<SendLogsWorker>(15, TimeUnit.MINUTES)
            .setInitialDelay(10, TimeUnit.SECONDS)
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniquePeriodicWork(WORK_NAME, ExistingPeriodicWorkPolicy.UPDATE, request)
    }

    private companion object {
        private const val WORK_NAME = "nosy-logger-send-logs"
    }
}