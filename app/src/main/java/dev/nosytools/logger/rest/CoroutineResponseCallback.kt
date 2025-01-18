package dev.nosytools.logger.rest

import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

internal class CoroutineResponseCallback(
    private val continuation: Continuation<ByteArray>
): Callback {

    override fun onFailure(call: Call, e: IOException) {
        continuation.resumeWithException(e)
    }

    override fun onResponse(call: Call, response: Response) {
        response.use {
            if (response.isSuccessful) {
                continuation.resume(
                    response.body?.bytes() ?: ByteArray(0)
                )
            } else {
                continuation.resumeWithException(
                    IOException("Unsuccessful response: ${response.code}")
                )
            }
        }
    }
}