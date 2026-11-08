package com.sadis.encyclopedia

import com.sadis.encyclopedia.data.IraqiCurriculumData
import com.sadis.encyclopedia.data.SubjectType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleRobolectricTest {

    @Test
    fun `verify all 7 Iraqi 6th Prep subjects exist and have verbatim pages`() {
        val allSubjects = SubjectType.entries
        assertEquals(7, allSubjects.size)

        for (subject in allSubjects) {
            val subjectPages = IraqiCurriculumData.textbookPages.filter { it.subject == subject }
            assertTrue("Subject ${subject.titleAr} must have textbook pages", subjectPages.isNotEmpty())

            // Every subject must have a Free First Chapter
            val freeChapterPages = subjectPages.filter { it.isFreeChapter && it.chapterOrder == 1 }
            assertTrue("Subject ${subject.titleAr} must have a free Chapter 1", freeChapterPages.isNotEmpty())

            // Verify every line has official ministerial questions + separated witty hint
            for (page in subjectPages) {
                assertTrue("Page ${page.pageId} must have lines", page.lines.isNotEmpty())
                assertTrue("Page ${page.pageId} must have a witty boost", page.pageWittyBoost.isNotBlank())
                for (line in page.lines) {
                    assertTrue("Line ${line.id} verbatim text must not be blank", line.verbatimText.isNotBlank())
                    assertTrue("Line ${line.id} must have ministerial questions", line.wizariAttachments.isNotEmpty())
                    for (qa in line.wizariAttachments) {
                        assertTrue("QA ${qa.id} must have a separated witty hint", qa.wittyHint.isNotBlank())
                        assertTrue("QA ${qa.id} must have control model answer", qa.controlModelAnswer.isNotBlank())
                    }
                }
            }
        }
    }

    @Test
    fun `verify chapter 1 is free and chapter 2 requires annual subscription`() {
        val lockedPages = IraqiCurriculumData.textbookPages.filter { it.chapterOrder >= 2 }
        assertTrue("There must be Chapter 2+ pages across subjects", lockedPages.isNotEmpty())
        for (page in lockedPages) {
            assertFalse("Chapter 2+ page ${page.pageId} must require annual subscription", page.isFreeChapter)
        }

        val plans = IraqiCurriculumData.subscriptionPlans
        assertTrue("Must have subscription tiers configured", plans.size >= 3)
        assertEquals("Exactly one plan must be recommended", 1, plans.count { it.isRecommended })
        val single = plans.first { it.planId == "SINGLE_SUBJECT_ANNUAL" }
        assertTrue("Single-subject plan should mention 30,000 IQD", single.priceIqd.contains("30,000"))
        val bundle = plans.first { it.planId == "ALL_7_SUBJECTS_VIP_ANNUAL" }
        assertTrue("Bundle plan should mention 150,000 IQD", bundle.priceIqd.contains("150,000"))
    }

    @Test
    fun `verify 3D interactive models have hotspots and witty memory hooks`() {
        val models = IraqiCurriculumData.interactive4KModels
        assertTrue("Must have interactive 3D/4K models", models.size >= 3)
        for (model in models) {
            assertTrue("Model ${model.modelId} must have hotspots", model.parts.isNotEmpty())
            for (part in model.parts) {
                assertTrue("Hotspot ${part.partId} must have textbook location", part.locationTextbook.isNotBlank())
                assertTrue("Hotspot ${part.partId} must have textbook function", part.functionTextbook.isNotBlank())
                assertTrue("Hotspot ${part.partId} must have witty memory hook", part.wittyMemoryHook.isNotBlank())
            }
        }
    }
}
