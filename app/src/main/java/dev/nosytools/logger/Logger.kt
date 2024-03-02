package dev.nosytools.logger

import android.content.Context
import dev.nosytools.logger.crypto.DiffieHellman
import dev.nosytools.logger.crypto.Encryptor
import dev.nosytools.logger.grpc.Collector
import dev.nosytools.logger.scheduler.Scheduler
import nosy_logger.LoggerOuterClass
import nosy_logger.LoggerOuterClass.Log
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security

class Logger(private val context: Context, private val apiKey: String) {

    init {
        Security.removeProvider("BC");
        Security.addProvider(BouncyCastleProvider())
    }

    private val diffieHellman by lazy { DiffieHellman() }
    private val scheduler by lazy { Scheduler(context, apiKey) }

    private lateinit var encryptor: Encryptor

    suspend fun init() {
        if (this::encryptor.isInitialized) {
            throw IllegalStateException("Already initialized")
        }

        val remotePublicKey = Collector(apiKey).handshake()

        encryptor = Encryptor(
            sharedSecretKey = diffieHellman.sharedSecret(remotePublicKey)
        )
    }

    fun debug(message: String) = log(message, LoggerOuterClass.Level.LEVEL_DEBUG)

    fun info(message: String) = log(message, LoggerOuterClass.Level.LEVEL_INFO)

    fun warning(message: String) = log(message, LoggerOuterClass.Level.LEVEL_WARN)

    fun error(message: String) = log(message, LoggerOuterClass.Level.LEVEL_ERROR)

    fun exception(throwable: Throwable) = error(throwable.message ?: "$throwable")

    private fun log(message: String, level: LoggerOuterClass.Level) {
        if (!this::encryptor.isInitialized) {
            throw IllegalStateException("Not initialized - make sure to call init() before you start logging")
        }

        val log = Log.newBuilder()
            .setDate(now())
            .setLevel(level)
            .setMessage(encryptor.encrypt(message))
            .setPublicKey(diffieHellman.publicKey)
            .build()

        scheduler.schedule(log)
    }
}
