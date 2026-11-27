package com.example.ui

import com.example.data.TextbookLine

enum class LineChallengeState { IN_PROGRESS, SOLVED, WRONG_ORDER }

/**
 * The key blocks a student must tap for a textbook line, in the order they appear in the book
 * sentence (or in the model answer when the line has fewer than two key terms).
 */
fun lineChallengeBlocks(line: TextbookLine): List<String> {
    if (line.keyTermsToCloze.size >= 2) {
        return line.keyTermsToCloze
            .distinct()
            .withIndex()
            .sortedWith(
                compareBy<IndexedValue<String>>(
                    { line.verbatimText.indexOf(it.value).let { position -> if (position < 0) Int.MAX_VALUE else position } },
                    { it.index }
                )
            )
            .map { it.value }
    }
    val rawAnswer = line.wizariAttachments.firstOrNull()?.controlModelAnswer ?: line.verbatimText
    return rawAnswer.split("،", "؛", ".")
        .map { it.trim() }
        .filter { it.length > 4 }
        .distinct()
        .take(4)
}

/** Solved only when every block was tapped in the book's order. */
fun lineChallengeState(picked: List<String>, target: List<String>): LineChallengeState = when {
    target.isEmpty() || picked.size < target.size -> LineChallengeState.IN_PROGRESS
    picked == target -> LineChallengeState.SOLVED
    else -> LineChallengeState.WRONG_ORDER
}
