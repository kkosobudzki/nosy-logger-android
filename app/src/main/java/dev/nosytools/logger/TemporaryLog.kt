package dev.nosytools.logger

import dev.nosytools.logger.crypto.Encryptor
import nosytools.logger.Logger.Level
import nosytools.logger.Logger.Log

internal data class TemporaryLog(val message: String, val level: Level, val date: String) {

    internal fun encrypt(encryptor: Encryptor): Log =
        Log.newBuilder()
            .setDate(date)
            .setLevel(level)
            .setMessage(encryptor.encrypt(message))
            .setPublicKey(encryptor.publicKey)
            .build()
}