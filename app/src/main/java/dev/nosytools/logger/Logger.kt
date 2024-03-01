package dev.nosytools.logger

import dev.nosytools.logger.crypto.DiffieHellman
import dev.nosytools.logger.crypto.Encryptor
import dev.nosytools.logger.grpc.CoroutineStreamObserver
import io.grpc.ManagedChannelBuilder
import io.grpc.Metadata
import io.grpc.stub.MetadataUtils
import nosy_logger.LoggerGrpc
import nosy_logger.LoggerGrpc.LoggerStub
import nosy_logger.LoggerOuterClass.Empty
import nosy_logger.LoggerOuterClass.Log
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security
import kotlin.coroutines.suspendCoroutine

class Logger(private val config: Config) {

    init {
        Security.removeProvider("BC");
        Security.addProvider(BouncyCastleProvider())
    }

    private val stub: LoggerStub by lazy {
        val headers = Metadata().apply {
            put(API_KEY_METADATA, config.apiKey)
        }

        ManagedChannelBuilder.forTarget(config.url)
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

    suspend fun log(logs: List<Log>) {
        if (!this::encryptor.isInitialized) {
            throw IllegalStateException("Not initialized - make sure to call init() before you start logging")
        }

        suspendCoroutine { continuation ->
            logs.map(::encrypt)
                .toLogs()
                .also {
                    stub.log(it, CoroutineStreamObserver(continuation))
                }
        }
    }

    private fun encrypt(log: Log): Log =
        Log.newBuilder()
            .setDate(log.date)
            .setLevel(log.level)
            .setMessage(log.message.encrypt())
            .setPublicKey(diffieHellman.publicKey)
            .build()

    private fun String.encrypt(): String =
        encryptor.encrypt(this)

    private companion object {
        val API_KEY_METADATA: Metadata.Key<String> =
            Metadata.Key.of("api-key", Metadata.ASCII_STRING_MARSHALLER)
    }
}
