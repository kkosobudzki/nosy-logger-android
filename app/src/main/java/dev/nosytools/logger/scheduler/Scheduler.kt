package dev.nosytools.logger.scheduler

import dev.nosytools.logger.grpc.Collector
import nosy_logger.LoggerOuterClass.Log

internal class Scheduler(private val collector: Collector) {

    fun schedule(log: Log) {
//        val logs = listOf(log).toLogs()
//
//        suspendCoroutine { continuation ->
//            stub.log(logs, CoroutineStreamObserver(continuation))
//        }

        // TODO save it for later
    }
}