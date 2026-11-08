package com.sadis.encyclopedia

import com.sadis.encyclopedia.data.SubjectType
import com.sadis.encyclopedia.data.SubscriptionStatusEntity
import com.sadis.encyclopedia.data.VoucherVerifier
import com.sadis.encyclopedia.data.VoucherVerifier.Result
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class VoucherVerifierTest {

    private val code = "0123456789ABCDEF"
    private val hash = VoucherVerifier.hashOf(code)
    private val before = 1_700_000_000_000L // 2023-11-14
    private val registry = VoucherVerifier.parseRegistry(
        sequenceOf(
            "# comment",
            "",
            "ALL_7_SUBJECTS_VIP_ANNUAL|*|2027-09-30|$hash",
            "garbage line",
            "SINGLE_SUBJECT_ANNUAL|physics|not-a-date|abc"
        )
    )

    @Test
    fun `hash matches python generator`() {
        // نفس القيمة التي يُنتجها tools/generate_vouchers.py
        assertEquals("942973c174db5f4da8268f940ad864880591567dbea0da734c7598be84cc8fae", hash)
    }

    @Test
    fun `epoch day conversion`() {
        assertEquals(0L, VoucherVerifier.parseIsoDateToEpochDay("1970-01-01"))
        assertEquals(11016L, VoucherVerifier.parseIsoDateToEpochDay("2000-02-29"))
        assertEquals(21091L, VoucherVerifier.parseIsoDateToEpochDay("2027-09-30"))
        assertNull(VoucherVerifier.parseIsoDateToEpochDay("2027-13-01"))
    }

    @Test
    fun `registry ignores comments and malformed lines`() {
        assertEquals(1, registry.size)
        val entry = registry.getValue(hash)
        assertNull(entry.subjectId)
        assertEquals((21091L + 1) * 86_400_000L, entry.expiresAtMillis)
    }

    @Test
    fun `valid code is accepted with dashes spaces and lowercase`() {
        val r = VoucherVerifier.verify(" 0123-4567-89ab-cdef ", registry, before)
        assertTrue(r is Result.Valid)
    }

    @Test
    fun `ambiguous letters are normalized`() {
        // O → 0 و I → 1 (الطالب قد يخلط بينها عند الكتابة)
        assertTrue(VoucherVerifier.verify("OI23456789ABCDEF", registry, before) is Result.Valid)
    }

    @Test
    fun `wrong format and unknown codes are rejected`() {
        assertEquals(Result.BadFormat, VoucherVerifier.verify("SADIS100", registry, before))
        assertEquals(Result.BadFormat, VoucherVerifier.verify("", registry, before))
        assertEquals(Result.NotFound, VoucherVerifier.verify("ZZZZZZZZZZZZZZZZ", registry, before))
    }

    @Test
    fun `expired code is rejected`() {
        val after = (21091L + 1) * 86_400_000L
        assertEquals(Result.Expired, VoucherVerifier.verify(code, registry, after))
    }

    @Test
    fun `single subject subscription unlocks only that subject`() {
        val status = SubscriptionStatusEntity(
            isAnnualVipUnlocked = true,
            activePlanId = "SINGLE_SUBJECT_ANNUAL",
            unlockedSubjectId = SubjectType.PHYSICS.id,
            expiresAt = before + 1000
        )
        assertTrue(status.unlocks(SubjectType.PHYSICS, before))
        assertFalse(status.unlocks(SubjectType.BIOLOGY, before))
        assertFalse("expired", status.unlocks(SubjectType.PHYSICS, before + 1000))
    }

    @Test
    fun `free tier unlocks nothing`() {
        val status = SubscriptionStatusEntity()
        SubjectType.entries.forEach { assertFalse(status.unlocks(it, before)) }
    }
}
