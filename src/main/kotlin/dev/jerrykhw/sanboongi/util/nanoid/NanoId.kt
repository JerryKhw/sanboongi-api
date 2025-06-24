package dev.jerrykhw.sanboongi.util.nanoid

import com.aventrix.jnanoid.jnanoid.NanoIdUtils
import java.security.SecureRandom

object NanoId {
    private val ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyz".toCharArray()
    private const val SIZE = 12
    private val random = SecureRandom()

    fun generate(): String {
        return NanoIdUtils.randomNanoId(random, ALPHABET, SIZE)
    }
}
