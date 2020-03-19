import android.content.Context
import android.text.TextUtils
import java.text.DecimalFormat
import java.util.regex.Pattern

object StringUtils {
    val PHONE_NUMBER_PATTERN1 = Pattern.compile("[+]?[9][8][0-9]{10}")
    val PHONE_NUMBER_PATTERN2 = Pattern.compile("[0]?[9][0-9]{9}")

    fun isValidPhone(phone: CharSequence): Boolean {
        return if (TextUtils.isEmpty(phone)) {
            false
        } else {
            PHONE_NUMBER_PATTERN1.matcher(phone).matches() ||
                    PHONE_NUMBER_PATTERN2.matcher(phone).matches()
        }
    }

    fun isPasswordLengthEnough(password: String): Boolean {
        return password.length >= 8
    }

    fun isNationalCodeValid(nationalCode: String): Boolean {
        if (!"^\\d{10}$".toRegex().matches(nationalCode))
            return false

        nationalCode.toCharArray().map(Character::getNumericValue).let {
            val check = it[9]
            val sum = (0..8).map { i -> it[i] * (10 - i) }.sum() % 11
            return sum < 2 && check == sum || sum >= 2 && check + sum == 11
        }
    }

    fun validateMelliCode(melliCode: String, context: Context): Boolean {
        when {
            melliCode.trim { it <= ' ' }.isEmpty() -> {
                return false // Melli Code is empty
            }
            melliCode.length != 10 -> {
                return false // Melli Code is less or more than 10 digits
            }
            else -> {
                var sum = 0

                for (i in 0..8) {
                    sum += Character.getNumericValue(melliCode[i]) * (10 - i)
                }

                val lastDigit: Int
                val divideRemaining = sum % 11
                lastDigit = if (divideRemaining < 2) divideRemaining else 11 - divideRemaining

                return Character.getNumericValue(melliCode[9]) == lastDigit
            }
        }
    }

    fun zipcodeValidation(zipcode: String): Boolean {
        return zipcode.length == 10
    }
}

val Int.threeGroup: String
    get() {
        val df = DecimalFormat()
        df.groupingSize = 3
        return df.format(this)
    }