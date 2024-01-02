package ru.kpfu.itis.ponomarev.androidcourse.util

import android.util.Patterns
import com.google.android.material.textfield.TextInputEditText
import kotlin.math.min

object Validators {

    fun normalizePhone(phone: String): String {
        return phone.replace(Regex("\\D"), "")
    }

    fun phoneNeedsFormatting(phone: String): Boolean {
        val regex =
            Regex("^\\+7 \\(9(\\d(\\d\\) (\\d(\\d(\\d-(\\d(\\d-(\\d{0,2})?)?)?)?)?)?)?)?\$")
        return !phone.matches(regex)
    }

    fun formatPhone(phone: String): String {
        // +7 (9##) ###-##-##
        val phoneDigits = normalizePhone(phone)
        val sb = StringBuilder()
        if (phoneDigits.isEmpty()) {
            return ""
        }
        sb.append("+7 (9")
        if (phoneDigits.length < 3) {
            return sb.toString()
        } else if (phoneDigits.length == 3) {
            sb.append(phoneDigits[2])
            return sb.toString()
        } else {
            sb.append(phoneDigits.substring(2, 4)).append(") ")
        }
        if (phoneDigits.length == 4) {
            return sb.toString()
        } else {
            sb.append(phoneDigits.substring(4, min(7, phoneDigits.length)))
        }
        if (phoneDigits.length < 7) {
            return sb.toString()
        } else {
            sb.append("-")
        }
        if (phoneDigits.length == 7) {
            return sb.toString()
        } else {
            sb.append(phoneDigits.substring(7, min(9, phoneDigits.length)))
        }
        if (phoneDigits.length < 9) {
            return sb.toString()
        } else {
            sb.append("-")
        }
        if (phoneDigits.length == 9) {
            return sb.toString()
        } else {
            sb.append(phoneDigits.substring(9, min(11, phoneDigits.length)))
        }
        return sb.toString()
    }

    fun validatePhone(phone: String): Boolean {
        return normalizePhone(phone).length == 11
    }

    fun validateEmail(email: String): Boolean {
        return email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}