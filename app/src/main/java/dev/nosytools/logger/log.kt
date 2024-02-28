package dev.nosytools.logger

import android.util.Log

internal fun String.log() = Log.d("NosyLogger", this)
