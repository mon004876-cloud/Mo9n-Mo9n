package com.example

import com.example.data.ActivationCodeRecord
import com.example.data.ActivationCodeRegistry
import com.example.data.ActivationCodes
import com.example.data.Entitlements
import com.example.data.PlanIds
import com.example.data.SubjectType
import com.example.data.SubscriptionStatusEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ActivationCodesTest {

    // Made with the same algorithm as tools/generate_activation_codes.py (fixed salt 00..0f), so this
    // test fails if the app and the generator ever stop agreeing. Code: TEST-7KQ2-M9XR-Z4PB
    private val physicsRecord = ActivationCodeRecord(
        id = "TEST",
        saltHex = "000102030405060708090a0b0c0d0e0f",
        hashHex = "04313334b8aefa2e3e4f88b274e16891a8c6c88a94a321a5cac02e06cdbae878",
        planId = PlanIds.SINGLE_SUBJECT_ANNUAL,
        subjectId = "physics"
    )
    private val records = listOf(physicsRecord)

    @Test
    fun `genuine code is accepted however the student types it`() {
        for (typed in listOf("TEST-7KQ2-M9XR-Z4PB", "test 7kq2 m9xr z4pb", "TEST7KQ2M9XRZ4PB", "TEST-٧KQ٢-M٩XR-Z٤PB")) {
            assertEquals(typed, physicsRecord, ActivationCodes.redeem(typed, records))
        }
    }

    @Test
    fun `wrong, altered or malformed codes are rejected`() {
        for (typed in listOf("TEST-7KQ2-M9XR-Z4PC", "TEST-7KQ2-M9XR-Z4P", "ABCD-7KQ2-M9XR-Z4PB", "", "SADIS100", "TEST-7KQ2-M9XR-Z4PU")) {
            assertNull(typed, ActivationCodes.redeem(typed, records))
        }
    }

    @Test
    fun `the developer demo code is not a real code`() {
        assertNull(ActivationCodes.redeem("SADIS100", ActivationCodeRegistry.records))
    }

    @Test
    fun `registry records are well formed`() {
        val records = ActivationCodeRegistry.records
        val ids = records.map { it.id }
        assertEquals("Duplicate code ids", ids.size, ids.toSet().size)
        for (record in records) {
            assertNotNull("Bad id ${record.id}", ActivationCodes.normalize(record.id + "000000000000"))
            assertEquals("Salt of ${record.id}", 32, record.saltHex.length)
            assertEquals("Hash of ${record.id}", 64, record.hashHex.length)
            val sellsAll = record.planId == PlanIds.ALL_SUBJECTS_ANNUAL && record.subjectId == null
            val sellsOne = record.planId == PlanIds.SINGLE_SUBJECT_ANNUAL && SubjectType.entries.any { it.id == record.subjectId }
            assertTrue("Plan of ${record.id}", sellsAll || sellsOne)
        }
    }
}

class EntitlementsTest {

    @Test
    fun `free tier unlocks nothing`() {
        assertFalse(Entitlements.isSubjectUnlocked(null, "physics"))
        assertFalse(Entitlements.isSubjectUnlocked(SubscriptionStatusEntity(), "physics"))
    }

    @Test
    fun `a single-subject code unlocks only that subject`() {
        val status = Entitlements.applyPurchase(null, PlanIds.SINGLE_SUBJECT_ANNUAL, "physics", 1L)!!
        assertTrue(Entitlements.isSubjectUnlocked(status, "physics"))
        assertFalse(Entitlements.isSubjectUnlocked(status, "chemistry"))
        assertFalse(Entitlements.hasAllSubjects(status))
    }

    @Test
    fun `a second single-subject code adds to the first`() {
        val physics = Entitlements.applyPurchase(null, PlanIds.SINGLE_SUBJECT_ANNUAL, "physics", 1L)
        val both = Entitlements.applyPurchase(physics, PlanIds.SINGLE_SUBJECT_ANNUAL, "chemistry", 2L)
        assertEquals(setOf("physics", "chemistry"), Entitlements.unlockedSubjectIds(both))
    }

    @Test
    fun `the all-subjects code unlocks every book and is never downgraded`() {
        val all = Entitlements.applyPurchase(null, PlanIds.ALL_SUBJECTS_ANNUAL, null, 1L)!!
        SubjectType.entries.forEach { assertTrue(it.id, Entitlements.isSubjectUnlocked(all, it.id)) }
        val afterSingle = Entitlements.applyPurchase(all, PlanIds.SINGLE_SUBJECT_ANNUAL, "physics", 2L)
        assertTrue(Entitlements.hasAllSubjects(afterSingle))
    }

    @Test
    fun `plans and subjects the app does not sell are refused`() {
        assertNull(Entitlements.applyPurchase(null, PlanIds.FREE_CHAPTER_1, null, 1L))
        assertNull(Entitlements.applyPurchase(null, PlanIds.SINGLE_SUBJECT_ANNUAL, null, 1L))
        assertNull(Entitlements.applyPurchase(null, PlanIds.SINGLE_SUBJECT_ANNUAL, "geography", 1L))
    }
}
