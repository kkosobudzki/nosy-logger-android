package dev.nosytools.logger.grpc

import dev.nosytools.logger.BuildConfig
import io.grpc.ManagedChannelBuilder
import io.grpc.Metadata
import io.grpc.stub.MetadataUtils
import nosy_logger.LoggerGrpc
import nosy_logger.LoggerOuterClass
import nosy_logger.LoggerOuterClass.Log
import kotlin.coroutines.suspendCoroutine

internal class Collector(private val apiKey: String) {

    private val stub: LoggerGrpc.LoggerStub by lazy {
        val headers = Metadata().apply {
            put(API_KEY_METADATA, apiKey)
        }

        ManagedChannelBuilder.forTarget(BuildConfig.COLLECTOR_URL)
            .useTransportSecurity()
            .build()
            .let(LoggerGrpc::newStub)
            .withInterceptors(MetadataUtils.newAttachHeadersInterceptor(headers))
    }

    internal suspend fun handshake(): String {
        val remotePublicKey = suspendCoroutine { continuation ->
            stub.handshake(
                LoggerOuterClass.Empty.newBuilder().build(),
                CoroutineStreamObserver(continuation)
            )
        }

        return remotePublicKey.key
    }

    internal suspend fun log(logs: List<Log>) {
        suspendCoroutine { continuation ->
            stub.log(logs.toLogs(), CoroutineStreamObserver(continuation))
        }
    }

    private fun List<Log>.toLogs(): LoggerOuterClass.Logs =
        LoggerOuterClass.Logs.newBuilder()
            .also { builder -> forEach(builder::addLogs) }
            .build()

    private companion object {
        val API_KEY_METADATA: Metadata.Key<String> =
            Metadata.Key.of("api-key", Metadata.ASCII_STRING_MARSHALLER)
    }
}