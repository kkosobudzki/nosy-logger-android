package dev.nosytools.logger.rest

import dev.nosytools.logger.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import nosytools.logger.Logger.Log
import nosytools.logger.Logger.Logs
import nosytools.logger.Logger.PublicKey
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import kotlin.coroutines.suspendCoroutine

internal class Collector(private val apiKey: String) {

    private val client by lazy { OkHttpClient() }

    private fun request(path: String): Request.Builder =
        Request.Builder()
            .header("accept", CONTENT_TYPE_PROTOBUF)
            .header("x-api-key", apiKey)
            .url("${BuildConfig.API_URL}${path}")

    internal suspend fun handshake(): String {
        val bytes = withContext(Dispatchers.IO) {
            suspendCoroutine { continuation ->
                request("/handshake")
                    .build()
                    .let(client::newCall)
                    .enqueue(CoroutineResponseCallback(continuation))
            }
        }

        return PublicKey.parseFrom(bytes).key
    }

    internal suspend fun log(logs: List<Log>) {
        val body = logs.toLogs()
            .toByteArray()
            .toRequestBody(contentType = CONTENT_TYPE_PROTOBUF.toMediaTypeOrNull())

        withContext(Dispatchers.IO) {
            suspendCoroutine { continuation ->
                request("/collect")
                    .post(body)
                    .build()
                    .let(client::newCall)
                    .enqueue(CoroutineResponseCallback(continuation))
            }
        }
    }

    private fun List<Log>.toLogs(): Logs =
        Logs.newBuilder()
            .also { builder -> forEach(builder::addLogs) }
            .build()

    private companion object {
        private const val CONTENT_TYPE_PROTOBUF = "application/x-protobuf"
    }
}