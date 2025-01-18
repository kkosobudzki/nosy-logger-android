package dev.nosytools.logger

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

internal fun now(): String =
    OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)