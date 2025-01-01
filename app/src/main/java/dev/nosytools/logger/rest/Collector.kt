package dev.nosytools.logger.rest

import dev.nosytools.logger.log
import nosytools.logger.Logger.Log

internal class Collector(private val apiKey: String) {

    internal suspend fun handshake(): String {
        "TODO handshake".log()

        return "TODO"
//        val remotePublicKey = withContext(Dispatchers.IO) {
//            suspendCoroutine { continuation ->
//                stub.handshake(
//                    LoggerOuterClass.Empty.newBuilder().build(),
//                    CoroutineStreamObserver(continuation)
//                )
//            }
//        }
//
//        return remotePublicKey.key
    }

    internal suspend fun log(logs: List<Log>) {
        "TODO log items".log()

//        withContext(Dispatchers.IO) {
//            suspendCoroutine { continuation ->
//                stub.log(logs.toLogs(), CoroutineStreamObserver(continuation))
//            }
//        }
    }

//    private fun List<Log>.toLogs(): LoggerOuterClass.Logs =
//        LoggerOuterClass.Logs.newBuilder()
//            .also { builder -> forEach(builder::addLogs) }
//            .build()
//
//    private companion object {
//        val API_KEY_METADATA: Metadata.Key<String> =
//            Metadata.Key.of("api-key", Metadata.ASCII_STRING_MARSHALLER)
//    }
}