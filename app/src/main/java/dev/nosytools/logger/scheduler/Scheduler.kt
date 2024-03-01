package dev.nosytools.logger.scheduler

import nosy_logger.LoggerGrpc.LoggerStub
import nosy_logger.LoggerOuterClass.Log

internal class Scheduler(private val stub: LoggerStub) {

    fun schedule(log: Log) {
//        val logs = listOf(log).toLogs()
//
//        suspendCoroutine { continuation ->
//            stub.log(logs, CoroutineStreamObserver(continuation))
//        }

        // TODO save it for later
    }
}