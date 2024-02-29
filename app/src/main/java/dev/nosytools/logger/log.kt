package dev.nosytools.logger

import android.util.Log

private const val TAG = "NosyLogger"

internal fun String.log() = Log.d(TAG, this)

internal fun String.error() = Log.e(TAG, this)
