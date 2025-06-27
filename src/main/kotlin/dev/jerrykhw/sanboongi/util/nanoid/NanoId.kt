package dev.jerrykhw.sanboongi.util.nanoid

import com.aventrix.jnanoid.jnanoid.NanoIdUtils
import java.security.SecureRandom

object NanoId {
    private val ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyz".toCharArray()
    private val random = SecureRandom()

    fun generate(): String {
        return NanoIdUtils.randomNanoId(random, ALPHABET, 12)
    }

    fun generateNickname(): String {
        return "산붕이-" + NanoIdUtils.randomNanoId(random, ALPHABET, 5)
    }
}
