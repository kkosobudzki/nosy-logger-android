package dev.nosytools.logger.scheduler

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dev.nosytools.logger.crypto.Encryptor
import dev.nosytools.logger.grpc.Collector
import dev.nosytools.logger.log
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class SendLogsWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params), KoinComponent {

    private val collector: Collector by inject()

    override suspend fun doWork(): Result {
        "SendLogsWorker::doWork".log()

        val logs = SharedBuffer.evict()

        if (logs.isNotEmpty()) {
            "SendLogsWorker::doWork -> sending".log()

            val remotePublicKey = collector.handshake()

            val encryptor = Encryptor(remotePublicKey)
            val encrypted = logs.map { log ->
                log.encrypt(encryptor)
            }

            collector.log(encrypted)
        }

        return Result.success()
    }
}