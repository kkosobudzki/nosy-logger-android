package dev.nosytools.logger

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

internal fun now(): String =
    LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)