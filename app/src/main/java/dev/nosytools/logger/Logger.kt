package dev.nosytools.logger

import dev.nosytools.logger.crypto.DiffieHellman
import dev.nosytools.logger.crypto.Encryptor
import dev.nosytools.logger.grpc.CoroutineStreamObserver
import dev.nosytools.logger.grpc.toLogs
import io.grpc.ManagedChannelBuilder
import io.grpc.Metadata
import io.grpc.stub.MetadataUtils
import nosy_logger.LoggerGrpc
import nosy_logger.LoggerGrpc.LoggerStub
import nosy_logger.LoggerOuterClass
import nosy_logger.LoggerOuterClass.Empty
import nosy_logger.LoggerOuterClass.Log
import now
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security
import kotlin.coroutines.suspendCoroutine

class Logger(private val apiKey: String) {

    init {
        Security.removeProvider("BC");
        Security.addProvider(BouncyCastleProvider())
    }

    private val stub: LoggerStub by lazy {
        val headers = Metadata().apply {
            put(API_KEY_METADATA, apiKey)
        }

        ManagedChannelBuilder.forTarget(BuildConfig.COLLECTOR_URL)
            .useTransportSecurity()
            .build()
            .let(LoggerGrpc::newStub)
            .withInterceptors(MetadataUtils.newAttachHeadersInterceptor(headers))
    }

    private val diffieHellman by lazy { DiffieHellman() }

    private lateinit var encryptor: Encryptor

    suspend fun init() {
        if (this::encryptor.isInitialized) {
            throw IllegalStateException("Already initialized")
        }

        val remotePublicKey = suspendCoroutine { continuation ->
            stub.handshake(
                Empty.newBuilder().build(),
                CoroutineStreamObserver(continuation)
            )
        }

        encryptor = Encryptor(
            sharedSecretKey = diffieHellman.sharedSecret(remotePublicKey.key)
        )
    }

    suspend fun debug(message: String) = log(message, LoggerOuterClass.Level.LEVEL_DEBUG)

    suspend fun info(message: String) = log(message, LoggerOuterClass.Level.LEVEL_INFO)

    suspend fun warning(message: String) = log(message, LoggerOuterClass.Level.LEVEL_WARN)

    suspend fun error(message: String) = log(message, LoggerOuterClass.Level.LEVEL_ERROR)

    suspend fun exception(throwable: Throwable) = error(throwable.message ?: "$throwable")

    private suspend fun log(message: String, level: LoggerOuterClass.Level) {
        if (!this::encryptor.isInitialized) {
            throw IllegalStateException("Not initialized - make sure to call init() before you start logging")
        }

        // TODO log to work manager and the send

        val log = Log.newBuilder()
            .setDate(now())
            .setLevel(level)
            .setMessage(message.encrypt())
            .setPublicKey(diffieHellman.publicKey)
            .build()

        val logs = listOf(log).toLogs()

        suspendCoroutine { continuation ->
            stub.log(logs, CoroutineStreamObserver(continuation))
        }
    }

    private fun String.encrypt(): String =
        encryptor.encrypt(this)

    private companion object {
        val API_KEY_METADATA: Metadata.Key<String> =
            Metadata.Key.of("api-key", Metadata.ASCII_STRING_MARSHALLER)
    }
}
