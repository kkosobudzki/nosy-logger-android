package dev.nosytools.logger

import dev.nosytools.logger.crypto.DiffieHellman
import dev.nosytools.logger.crypto.Encryptor
import dev.nosytools.logger.grpc.DelegatedStreamObserver
import io.grpc.ManagedChannelBuilder
import io.grpc.Metadata
import io.grpc.stub.MetadataUtils
import nosy_logger.LoggerGrpc
import nosy_logger.LoggerGrpc.LoggerStub
import nosy_logger.LoggerOuterClass.Empty
import nosy_logger.LoggerOuterClass.Log

class Logger(private val config: Config) {

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

  fun init(onCompleted: () -> Unit, onError: (Throwable?) -> Unit) {
    stub.handshake(
      Empty.newBuilder().build(),
      DelegatedStreamObserver(
        whenNext = { remotePublicKey ->
          encryptor = Encryptor(
            sharedSecretKey = diffieHellman.sharedSecret(remotePublicKey.key)
          )

          onCompleted()
        },
        whenError = onError
      )
    )
  }

  fun log(logs: List<Log>, onCompleted: () -> Unit, onError: (Throwable?) -> Unit) {
    logs.map(::encrypt)
      .toLogs()
      .also {
        stub.log(it, DelegatedStreamObserver(whenCompleted = onCompleted, whenError = onError))
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
