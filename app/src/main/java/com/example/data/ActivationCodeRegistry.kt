package com.example.data

/**
 * The activation codes you sell, stored as salted hashes (the codes themselves are never in the app).
 *
 * This list is empty on purpose: until you add records, a release build unlocks nothing, so paid
 * chapters cannot be opened for free. To create codes, run from the project folder:
 *
 *     python3 tools/generate_activation_codes.py --plan all --count 50
 *     python3 tools/generate_activation_codes.py --plan subject --subject physics --count 50
 *
 * The tool writes the printable codes to a private CSV file (keep it out of git) and prints the
 * Kotlin lines to paste below.
 */
object ActivationCodeRegistry {
    val records: List<ActivationCodeRecord> = listOf(
        // Paste generated ActivationCodeRecord(...) lines here.
    )
}
