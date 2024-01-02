package ru.kpfu.itis.ponomarev.androidcourse.util

import java.math.BigInteger
import java.security.MessageDigest

object PasswordUtil {

    private val md = MessageDigest.getInstance("MD5")

    fun encrypt(password: String): String {
        md.reset()
        md.update(password.toByteArray())
        return BigInteger(1, md.digest()).toString(16)
    }
}
