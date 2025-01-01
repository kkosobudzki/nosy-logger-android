package dev.nosytools.logger.rest

import dev.nosytools.logger.BuildConfig
import dev.nosytools.logger.log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import nosytools.logger.Logger.Log
import nosytools.logger.Logger.PublicKey
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlin.coroutines.suspendCoroutine

internal class Collector(private val apiKey: String) {

    private val client by lazy { OkHttpClient() }

    internal suspend fun handshake(): String {
        val request = Request.Builder()
            .header("x-api-key", apiKey)
            .url("${BuildConfig.API_URL}/handshake")
            .build()

        val bytes = withContext(Dispatchers.IO) {
            suspendCoroutine { continuation ->
                client.newCall(request)
                    .enqueue(CoroutineResponseCallback(continuation))
            }
        }

        return PublicKey.parseFrom(bytes).key
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
}