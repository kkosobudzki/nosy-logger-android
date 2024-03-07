package dev.nosytools.logger.scheduler

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dev.nosytools.logger.crypto.Encryptor
import dev.nosytools.logger.error
import dev.nosytools.logger.grpc.Collector
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class SendLogsWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params), KoinComponent {

    private val collector: Collector by inject()

    override suspend fun doWork(): Result {
        try {
            val logs = SharedBuffer.evict()

            if (logs.isNotEmpty()) {
                val remotePublicKey = collector.handshake()

                val encryptor = Encryptor(remotePublicKey)
                val encrypted = logs.map { log ->
                    log.encrypt(encryptor)
                }

                collector.log(encrypted)
            }
        } catch (e: Exception) {
            "SendLogsWorker - got error: ${e.message}".error()

            e.printStackTrace()

            return Result.failure()
        }

        return Result.success()
    }
}