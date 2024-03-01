package dev.nosytools.logger.grpc

import dev.nosytools.logger.error
import io.grpc.stub.StreamObserver

internal class DelegatedStreamObserver<T>(
  private val whenNext: (T) -> Unit = {},
  private val whenCompleted: () -> Unit = {},
  private val whenError: (Throwable) -> Unit = { t ->
    "DelegatedStreamObserver thrown error: ${t.message}".error()
  }
) : StreamObserver<T> {
  override fun onNext(value: T) {
    whenNext(value)
  }

  override fun onError(t: Throwable) {
    whenError(t)
  }

  override fun onCompleted() {
    whenCompleted()
  }
}
