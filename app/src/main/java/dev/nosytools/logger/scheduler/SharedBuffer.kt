package dev.nosytools.logger.scheduler

import nosy_logger.LoggerOuterClass.Log

internal object SharedBuffer {

    private val items: MutableList<Log> = mutableListOf()

    internal fun push(log: Log) {
        items.add(log)
    }

    internal fun evict(): List<Log> = items.filterTo(mutableListOf()) { true }
}