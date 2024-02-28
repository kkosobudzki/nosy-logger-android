package dev.nosytools.logger

import android.util.Log
import io.grpc.stub.StreamObserver

internal class DelegatedStreamObserver<T>(
  private val whenNext: (T) -> Unit = {},
  private val whenCompleted: () -> Unit = {},
  private val whenError: (Throwable?) -> Unit = { t ->
    Log.e(
      "NosyLogger",
      "DelegatedStreamObserver thrown error",
      t
    )
  }
) : StreamObserver<T> {
  override fun onNext(value: T) {
    whenNext(value)
  }

  override fun onError(t: Throwable?) {
    whenError(t)
  }

  override fun onCompleted() {
    whenCompleted()
  }
}
