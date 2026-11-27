package com.example.data

import java.security.MessageDigest
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/** Plan identifiers shared by the plan list, the activation codes and the stored subscription row. */
object PlanIds {
    const val FREE_CHAPTER_1 = "FREE_CHAPTER_1"
    const val SINGLE_SUBJECT_ANNUAL = "SINGLE_SUBJECT_ANNUAL"
    const val ALL_SUBJECTS_ANNUAL = "ALL_7_SUBJECTS_VIP_ANNUAL"
}

/**
 * One sellable activation code. The app only keeps a salted PBKDF2 hash of the code, so the
 * codes cannot be read back out of the APK. Create records with tools/generate_activation_codes.py.
 *
 * @param id the first group of the printed code; it is not secret and only locates the record.
 * @param subjectId the [SubjectType.id] a single-subject code unlocks; null for the all-subjects plan.
 */
data class ActivationCodeRecord(
    val id: String,
    val saltHex: String,
    val hashHex: String,
    val planId: String,
    val subjectId: String? = null
)

/**
 * Offline check of codes printed as `XXXX-XXXX-XXXX-XXXX` (Crockford base32, 16 characters).
 * Must stay in sync with tools/generate_activation_codes.py.
 */
object ActivationCodes {
    const val ALPHABET = "0123456789ABCDEFGHJKMNPQRSTVWXYZ"
    const val ID_LENGTH = 4
    const val SECRET_LENGTH = 12
    const val PBKDF2_ITERATIONS = 10_000
    private const val HASH_BITS = 256

    /**
     * Upper-cases the input, drops spaces and dashes, turns Arabic-Indic digits into 0-9 and the
     * look-alike letters O, I and L into 0 and 1. Returns null when it cannot be a code.
     */
    fun normalize(raw: String): String? {
        val cleaned = StringBuilder()
        for (ch in raw.uppercase()) {
            when (ch) {
                ' ', '-', ' ', '‏', '‎' -> Unit
                'O' -> cleaned.append('0')
                'I', 'L' -> cleaned.append('1')
                in '٠'..'٩' -> cleaned.append('0' + (ch - '٠'))
                in '۰'..'۹' -> cleaned.append('0' + (ch - '۰'))
                else -> cleaned.append(ch)
            }
        }
        if (cleaned.length != ID_LENGTH + SECRET_LENGTH) return null
        if (cleaned.any { it !in ALPHABET }) return null
        return cleaned.toString()
    }

    fun hashSecret(secret: String, salt: ByteArray): ByteArray {
        val spec = PBEKeySpec(secret.toCharArray(), salt, PBKDF2_ITERATIONS, HASH_BITS)
        return try {
            SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1").generateSecret(spec).encoded
        } finally {
            spec.clearPassword()
        }
    }

    /** Returns the record [rawCode] unlocks, or null when the code is not one of [records]. */
    fun redeem(rawCode: String, records: List<ActivationCodeRecord>): ActivationCodeRecord? {
        val code = normalize(rawCode) ?: return null
        val id = code.substring(0, ID_LENGTH)
        val secret = code.substring(ID_LENGTH)
        val record = records.firstOrNull { it.id == id } ?: return null
        val salt = hexToBytes(record.saltHex) ?: return null
        val expected = hexToBytes(record.hashHex) ?: return null
        return if (MessageDigest.isEqual(hashSecret(secret, salt), expected)) record else null
    }

    internal fun hexToBytes(hex: String): ByteArray? {
        if (hex.isEmpty() || hex.length % 2 != 0) return null
        val bytes = ByteArray(hex.length / 2)
        for (i in bytes.indices) {
            val high = Character.digit(hex[2 * i], 16)
            val low = Character.digit(hex[2 * i + 1], 16)
            if (high < 0 || low < 0) return null
            bytes[i] = ((high shl 4) or low).toByte()
        }
        return bytes
    }
}

/**
 * Which books a student has paid for. Single-subject purchases are kept in the existing
 * `activePlanId` column as `SINGLE_SUBJECT_ANNUAL:physics,chemistry`, so no database migration
 * is needed.
 */
object Entitlements {
    private const val SINGLE_PREFIX = PlanIds.SINGLE_SUBJECT_ANNUAL + ":"

    fun hasAllSubjects(status: SubscriptionStatusEntity?): Boolean = status?.isAnnualVipUnlocked == true

    fun unlockedSubjectIds(status: SubscriptionStatusEntity?): Set<String> {
        val planId = status?.activePlanId ?: return emptySet()
        if (!planId.startsWith(SINGLE_PREFIX)) return emptySet()
        return planId.removePrefix(SINGLE_PREFIX)
            .split(',')
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .toSet()
    }

    fun isSubjectUnlocked(status: SubscriptionStatusEntity?, subjectId: String): Boolean =
        hasAllSubjects(status) || subjectId in unlockedSubjectIds(status)

    /**
     * The subscription row to store after a valid code for [planId] is redeemed. Never takes away
     * anything the student already has. Returns null for a plan this app does not sell.
     */
    fun applyPurchase(
        current: SubscriptionStatusEntity?,
        planId: String,
        subjectId: String?,
        nowMillis: Long
    ): SubscriptionStatusEntity? {
        val base = current ?: SubscriptionStatusEntity()
        return when {
            planId == PlanIds.ALL_SUBJECTS_ANNUAL -> base.copy(
                isAnnualVipUnlocked = true,
                activePlanId = PlanIds.ALL_SUBJECTS_ANNUAL,
                activatedAt = nowMillis
            )

            planId == PlanIds.SINGLE_SUBJECT_ANNUAL && subjectId != null &&
                SubjectType.entries.any { it.id == subjectId } -> {
                if (hasAllSubjects(base)) {
                    base
                } else {
                    val subjects = (unlockedSubjectIds(base) + subjectId).sorted()
                    base.copy(
                        isAnnualVipUnlocked = false,
                        activePlanId = SINGLE_PREFIX + subjects.joinToString(","),
                        activatedAt = nowMillis
                    )
                }
            }

            else -> null
        }
    }
}
