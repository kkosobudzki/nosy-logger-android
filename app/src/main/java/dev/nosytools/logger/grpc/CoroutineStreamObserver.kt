package dev.nosytools.logger.grpc

import io.grpc.stub.StreamObserver
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

internal class CoroutineStreamObserver<T>(private val continuation: Continuation<T>): StreamObserver<T> {
    override fun onNext(value: T) {
        continuation.resume(value)
    }

    override fun onError(t: Throwable) {
        continuation.resumeWithException(t)
    }

    override fun onCompleted() {
        // unsupported
    }
}