package dev.nosytools.logger

import android.util.Base64

internal fun ByteArray.encode(): String =
  Base64.encodeToString(this, Base64.NO_WRAP)

internal fun String.decode(): ByteArray =
  Base64.decode(this, Base64.DEFAULT)
