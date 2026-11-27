package com.example

import com.example.data.IraqiCurriculumData
import com.example.data.PlanIds
import com.example.data.SubjectType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CurriculumIntegrityTest {

    private val pages = IraqiCurriculumData.textbookPages
    private val models = IraqiCurriculumData.interactive4KModels

    @Test
    fun `every cloze term appears in its textbook line`() {
        for (page in pages) {
            for (line in page.lines) {
                for (term in line.keyTermsToCloze) {
                    assertTrue(
                        "Line ${line.id}: cloze term '$term' is not in the text, so recall mode can never hide it",
                        line.verbatimText.contains(term)
                    )
                }
            }
        }
    }

    @Test
    fun `every 3D link points to an existing model`() {
        val modelIds = models.map { it.modelId }.toSet()
        val linkedIds = pages.flatMap { page ->
            page.lines.mapNotNull { it.linked3DModelId } + listOfNotNull(page.bookFigure?.linked3DModelId)
        }
        for (id in linkedIds) {
            assertTrue("Unknown 3D model id '$id'", id in modelIds)
        }
    }

    @Test
    fun `ids used as list keys are unique`() {
        assertUnique("page id", pages.map { it.pageId })
        assertUnique("line id", pages.flatMap { page -> page.lines.map { it.id } })
        assertUnique("ministerial question id", pages.flatMap { page -> page.lines.flatMap { line -> line.wizariAttachments.map { it.id } } })
        assertUnique("model id", models.map { it.modelId })
        models.forEach { model -> assertUnique("part id in ${model.modelId}", model.parts.map { it.partId }) }
        assertUnique("plan id", IraqiCurriculumData.subscriptionPlans.map { it.planId })
    }

    @Test
    fun `prices agree on every screen`() {
        val plans = IraqiCurriculumData.subscriptionPlans.associateBy { it.planId }
        assertTrue("Free plan missing", plans.containsKey(PlanIds.FREE_CHAPTER_1))
        val single = plans.getValue(PlanIds.SINGLE_SUBJECT_ANNUAL)
        val bundle = plans.getValue(PlanIds.ALL_SUBJECTS_ANNUAL)
        for (subject in SubjectType.entries) {
            assertEquals("Price of ${subject.id} differs from the single-book plan", single.priceIqd, subject.singleSubjectPriceIqd)
        }
        val singlePrice = firstAmount(single.priceIqd)
        val bundlePrice = firstAmount(bundle.priceIqd)
        assertTrue(
            "The 7-book bundle ($bundlePrice) must cost less than 7 single books (${7 * singlePrice})",
            bundlePrice < 7 * singlePrice
        )
    }

    private fun firstAmount(price: String): Int =
        Regex("[0-9][0-9,]*").find(price)!!.value.replace(",", "").toInt()

    private fun assertUnique(label: String, values: List<String>) {
        val duplicates = values.groupBy { it }.filterValues { it.size > 1 }.keys
        assertTrue("Duplicate $label: $duplicates", duplicates.isEmpty())
    }
}
