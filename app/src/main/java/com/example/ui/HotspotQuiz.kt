package com.example.ui

/**
 * Progress through the pin-drop quiz of one 3D model. A part only scores when it is found on the
 * first try, so the final score says how many labels the student really knew.
 */
data class HotspotQuizProgress(
    val targetIndex: Int = 0,
    val score: Int = 0,
    val missedCurrentTarget: Boolean = false,
    val isCompleted: Boolean = false
)

enum class HotspotTapOutcome { CORRECT, WRONG, IGNORED }

data class HotspotQuizStep(val progress: HotspotQuizProgress, val outcome: HotspotTapOutcome)

fun HotspotQuizProgress.onPartTapped(tappedPartId: String, partIds: List<String>): HotspotQuizStep {
    if (isCompleted || targetIndex !in partIds.indices) {
        return HotspotQuizStep(this, HotspotTapOutcome.IGNORED)
    }
    if (tappedPartId != partIds[targetIndex]) {
        return HotspotQuizStep(copy(missedCurrentTarget = true), HotspotTapOutcome.WRONG)
    }
    val nextIndex = targetIndex + 1
    val finished = nextIndex >= partIds.size
    return HotspotQuizStep(
        HotspotQuizProgress(
            targetIndex = if (finished) targetIndex else nextIndex,
            score = if (missedCurrentTarget) score else score + 1,
            missedCurrentTarget = false,
            isCompleted = finished
        ),
        HotspotTapOutcome.CORRECT
    )
}
