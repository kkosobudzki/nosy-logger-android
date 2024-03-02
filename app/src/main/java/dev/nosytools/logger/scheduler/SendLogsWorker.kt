package dev.nosytools.logger.scheduler

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import dev.nosytools.logger.grpc.Collector
import dev.nosytools.logger.log

internal class SendLogsWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        "SendLogsWorker::doWork".log()

        val arguments = Arguments.from(inputData)
        val logs = SharedBuffer.evict()

        if (logs.isNotEmpty()) {
            "SendLogsWorker::doWork -> sending".log()

            Collector(arguments.apiKey).log(logs)
        }

        return Result.success()
    }

    internal data class Arguments(val apiKey: String) {
        internal fun serialize() = workDataOf(API_KEY to apiKey)

        internal companion object {
            private const val API_KEY = "API_KEY"

            internal fun from(data: Data) =
                Arguments(apiKey = data.getString(API_KEY).orEmpty())
        }
    }
}