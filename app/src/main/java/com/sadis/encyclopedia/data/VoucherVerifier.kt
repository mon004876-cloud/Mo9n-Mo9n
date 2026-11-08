package com.sadis.encyclopedia.data

import java.security.MessageDigest

/**
 * التحقق من أكواد التفعيل السنوية.
 *
 * التطبيق لا يحتوي على الأكواد نفسها، بل على بصمة SHA-256 لكل كود فقط
 * (ملف assets/vouchers/voucher_registry.txt). لذلك لا يمكن استخراج كود صالح من ملف الـ APK.
 * الأكواد تُولَّد بالسكربت tools/generate_vouchers.py وتُوزَّع على المكتبات أو بعد الدفع عبر زين كاش.
 *
 * صيغة كل سطر في السجل:
 *   PLAN_ID|SUBJECT_ID_OR_*|EXPIRY_YYYY-MM-DD|SHA256_HEX
 *
 * ملاحظة: التحقق محلي (Offline). منع استخدام الكود نفسه على أكثر من جهاز يحتاج خادماً،
 * وهذا موضّح في README.
 */
object VoucherVerifier {

    private const val HASH_PREFIX = "sadis-voucher-v1:"
    private const val CODE_LENGTH = 16
    private val ALLOWED = Regex("^[0-9A-HJKMNP-TV-Z]{$CODE_LENGTH}$") // Crockford Base32

    data class RegistryEntry(
        val planId: String,
        val subjectId: String?, // null = جميع المواد
        val expiresAtMillis: Long,
        val hash: String
    )

    sealed interface Result {
        data class Valid(val entry: RegistryEntry) : Result
        data object BadFormat : Result
        data object NotFound : Result
        data object Expired : Result
    }

    /** يحوّل ما يكتبه الطالب إلى الصيغة القياسية: أحرف كبيرة، بدون مسافات أو شرطات، O→0 و I/L→1. */
    fun normalize(raw: String): String = raw
        .uppercase()
        .filter { it.isLetterOrDigit() }
        .map {
            when (it) {
                'O' -> '0'
                'I', 'L' -> '1'
                else -> it
            }
        }
        .joinToString("")

    fun hashOf(normalizedCode: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
            .digest((HASH_PREFIX + normalizedCode).toByteArray(Charsets.UTF_8))
        return digest.joinToString("") { "%02x".format(it) }
    }

    fun parseRegistry(lines: Sequence<String>): Map<String, RegistryEntry> = lines
        .map { it.trim() }
        .filter { it.isNotEmpty() && !it.startsWith("#") }
        .mapNotNull { line ->
            val parts = line.split('|')
            if (parts.size != 4) return@mapNotNull null
            // الكود صالح حتى نهاية يوم الانتهاء (بتوقيت UTC)
            val expiry = parseIsoDateToEpochDay(parts[2])?.let { (it + 1) * DAY_MILLIS }
                ?: return@mapNotNull null
            RegistryEntry(
                planId = parts[0],
                subjectId = parts[1].takeUnless { it == "*" },
                expiresAtMillis = expiry,
                hash = parts[3].lowercase()
            )
        }
        .associateBy { it.hash }

    private const val DAY_MILLIS = 24L * 60 * 60 * 1000

    /** تحويل YYYY-MM-DD إلى رقم اليوم منذ 1970-01-01 بدون java.time (غير متوفر قبل API 26). */
    internal fun parseIsoDateToEpochDay(text: String): Long? {
        val m = Regex("^(\\d{4})-(\\d{2})-(\\d{2})$").matchEntire(text) ?: return null
        val y0 = m.groupValues[1].toLong()
        val mo = m.groupValues[2].toInt()
        val d = m.groupValues[3].toInt()
        if (mo !in 1..12 || d !in 1..31) return null
        // خوارزمية Howard Hinnant (days_from_civil)
        val y = if (mo <= 2) y0 - 1 else y0
        val era = Math.floorDiv(y, 400L)
        val yoe = y - era * 400
        val mp = (mo + 9) % 12
        val doy = (153 * mp + 2) / 5 + d - 1
        val doe = yoe * 365 + yoe / 4 - yoe / 100 + doy
        return era * 146097 + doe - 719468
    }

    fun verify(rawCode: String, registry: Map<String, RegistryEntry>, nowMillis: Long): Result {
        val code = normalize(rawCode)
        if (!ALLOWED.matches(code)) return Result.BadFormat
        val entry = registry[hashOf(code)] ?: return Result.NotFound
        if (nowMillis >= entry.expiresAtMillis) return Result.Expired
        return Result.Valid(entry)
    }
}
