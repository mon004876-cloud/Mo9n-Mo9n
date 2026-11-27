package com.example

import com.example.data.IraqiCurriculumData
import com.example.data.TextbookLine
import com.example.ui.HotspotQuizProgress
import com.example.ui.HotspotTapOutcome
import com.example.ui.LineChallengeState
import com.example.ui.lineChallengeBlocks
import com.example.ui.lineChallengeState
import com.example.ui.onPartTapped
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HotspotQuizTest {

    private val parts = listOf("a", "b", "c")

    private fun play(vararg taps: String): HotspotQuizProgress =
        taps.fold(HotspotQuizProgress()) { progress, tap -> progress.onPartTapped(tap, parts).progress }

    @Test
    fun `a perfect run scores full marks`() {
        val result = play("a", "b", "c")
        assertTrue(result.isCompleted)
        assertEquals(3, result.score)
    }

    @Test
    fun `a part found only after a wrong tap does not score`() {
        val result = play("b", "a", "b", "c")
        assertTrue(result.isCompleted)
        assertEquals(2, result.score)
    }

    @Test
    fun `taps after the quiz is finished are ignored`() {
        val finished = play("a", "b", "c")
        val step = finished.onPartTapped("a", parts)
        assertEquals(HotspotTapOutcome.IGNORED, step.outcome)
        assertEquals(finished, step.progress)
    }
}

class LineChallengeTest {

    private fun line(text: String, vararg terms: String) = TextbookLine(
        id = "line",
        lineIndex = 1,
        verbatimText = text,
        keyTermsToCloze = terms.toList(),
        wizariAttachments = emptyList()
    )

    @Test
    fun `blocks follow the order of the textbook sentence`() {
        assertEquals(listOf("alpha", "beta", "gamma"), lineChallengeBlocks(line("alpha beta gamma", "gamma", "alpha", "beta")))
    }

    @Test
    fun `only the book order solves the challenge`() {
        val target = listOf("alpha", "beta", "gamma")
        assertEquals(LineChallengeState.IN_PROGRESS, lineChallengeState(listOf("alpha"), target))
        assertEquals(LineChallengeState.WRONG_ORDER, lineChallengeState(listOf("beta", "alpha", "gamma"), target))
        assertEquals(LineChallengeState.SOLVED, lineChallengeState(target, target))
    }

    @Test
    fun `every textbook line has a challenge to play`() {
        for (page in IraqiCurriculumData.textbookPages) {
            for (textbookLine in page.lines) {
                assertTrue(textbookLine.id, lineChallengeBlocks(textbookLine).size >= 2)
            }
        }
    }
}
