package dev.nosytools.logger

import dev.nosytools.logger.crypto.Encryptor
import nosy_logger.LoggerOuterClass.Level
import nosy_logger.LoggerOuterClass.Log

internal data class TemporaryLog(val message: String, val level: Level, val date: String) {

    // TODO maybe make diffie hellman part of encryptor?
    fun encrypt(encryptor: Encryptor, publicKey: String): Log =
        Log.newBuilder()
            .setDate(date)
            .setLevel(level)
            .setMessage(encryptor.encrypt(message))
            .setPublicKey(publicKey)
            .build()
}