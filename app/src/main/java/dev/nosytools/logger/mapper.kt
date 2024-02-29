package dev.nosytools.logger

import nosy_logger.LoggerOuterClass.Log
import nosy_logger.LoggerOuterClass.Logs

internal fun List<Log>.toLogs(): Logs =
  Logs.newBuilder()
    .also { builder -> forEach(builder::addLogs) }
    .build()
