package dev.nosytools.logger

import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PublicKey
import java.security.spec.ECGenParameterSpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.KeyAgreement
import javax.crypto.SecretKey


internal class DiffieHellman {

  private val keyPair: KeyPair by lazy {
    KeyPairGenerator.getInstance(ALGORITHM, PROVIDER)
      .apply {
        initialize(ECGenParameterSpec(KEY_PAIR_CURVE))
      }
      .generateKeyPair()
  }

  internal val publicKey: String by lazy {
    keyPair.public
      .encoded
      .encode()
  }

  internal fun sharedSecret(otherPublicKey: String): SecretKey =
    KeyAgreement.getInstance(ALGORITHM, PROVIDER)
      .apply {
        init(keyPair.private)

        doPhase(otherPublicKey.toPublicKey(), true)
      }
      .generateSecret(ALGORITHM)

  private fun String.toPublicKey(): PublicKey =
    decode()
      .let(::X509EncodedKeySpec)
      .let(KeyFactory.getInstance(ALGORITHM, PROVIDER)::generatePublic)

  private companion object {
    private const val ALGORITHM = "ECDH"
    private const val PROVIDER = "BC"
    private const val KEY_PAIR_CURVE = "prime256v1"
  }
}
