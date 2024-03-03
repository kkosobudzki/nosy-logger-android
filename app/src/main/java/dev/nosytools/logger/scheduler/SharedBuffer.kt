package dev.nosytools.logger.scheduler

import dev.nosytools.logger.TemporaryLog

internal object SharedBuffer {

    private val items: MutableList<TemporaryLog> = mutableListOf()

    internal fun push(log: TemporaryLog) {
        items.add(log)
    }

    internal fun evict(): List<TemporaryLog> = items.toList().also { items.clear() }
}