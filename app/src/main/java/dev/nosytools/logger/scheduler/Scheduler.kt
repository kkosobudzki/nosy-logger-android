package dev.nosytools.logger.scheduler

import androidx.work.OneTimeWorkRequestBuilder
import nosy_logger.LoggerOuterClass.Log
import java.util.concurrent.TimeUnit

internal class Scheduler(private val apiKey: String) {

    fun schedule(log: Log) {
        SharedBuffer.push(log)

        val data = SendLogsWorker.Arguments(apiKey).serialize()

        val request = OneTimeWorkRequestBuilder<SendLogsWorker>()
            .setInitialDelay(10, TimeUnit.SECONDS)
            .setInputData(data)
            .build()

        // TODO start periodic worker (if not started)
    }
}