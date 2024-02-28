package dev.nosytools.logger

import nosy_logger.LoggerOuterClass.Level
import nosy_logger.LoggerOuterClass.Log
import nosy_logger.LoggerOuterClass.Logs

private fun String.toLevel(): Level =
  when (this) {
    "debug" -> Level.LEVEL_DEBUG
    "info" -> Level.LEVEL_INFO
    "warn" -> Level.LEVEL_WARN
    "error" -> Level.LEVEL_ERROR
    else -> throw IllegalArgumentException("Unsupported log level")
  }

internal fun List<Log>.toLogs(): Logs =
  Logs.newBuilder()
    .also { builder -> forEach(builder::addLogs) }
    .build()
