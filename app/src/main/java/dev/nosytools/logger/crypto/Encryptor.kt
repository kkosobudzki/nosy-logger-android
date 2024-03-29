package dev.nosytools.logger.crypto

import java.nio.ByteBuffer
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec

internal class Encryptor(private val remotePublicKey: String) {

    private val diffieHellman by lazy { DiffieHellman() }
    private val sharedSecretKey by lazy { diffieHellman.deriveSharedSecret(remotePublicKey) }

    internal val publicKey by lazy { diffieHellman.publicKey }

    fun encrypt(input: String, nonce: ByteArray = generateNonce()): String =
        Cipher.getInstance(CIPHER_ALGORITHM)
            .apply {
                init(Cipher.ENCRYPT_MODE, sharedSecretKey, IvParameterSpec(nonce))
            }
            .doFinal(input.toByteArray())
            .let { encrypted ->
                ByteBuffer.allocate(nonce.size + encrypted.size)
                    .put(nonce)
                    .put(encrypted)
                    .array()
            }
            .encode()

    private fun generateNonce(): ByteArray =
        SecureRandom().generateSeed(NONCE_LENGTH)

    internal companion object {
        private const val CIPHER_ALGORITHM = "ChaCha20-Poly1305"
        private const val NONCE_LENGTH = 12
    }
}
