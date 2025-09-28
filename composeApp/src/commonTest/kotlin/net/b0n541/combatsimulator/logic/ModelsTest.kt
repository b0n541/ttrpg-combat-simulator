package net.b0n541.combatsimulator.logic

import ch.tutteli.atrium.api.fluent.en_GB.toEqual
import ch.tutteli.atrium.api.verbs.expect
import kotlin.test.Test

class ModelsTest {

    @Test
    fun abilityScoreModifier() {
        val testCases = mapOf(
            1 to -5, 16 to 3,
            2 to -4, 17 to 3,
            3 to -4, 18 to 4,
            4 to -3, 19 to 4,
            5 to -3, 20 to 5,
            6 to -2, 21 to 5,
            7 to -2, 22 to 6,
            8 to -1, 23 to 6,
            9 to -1, 24 to 7,
            10 to 0, 25 to 7,
            11 to 0, 26 to 8,
            12 to 1, 27 to 8,
            13 to 1, 28 to 9,
            14 to 2, 29 to 9,
            15 to 2, 30 to 10
        )

        testCases.forEach { (score, expectedModifier) ->
            val modifier = AbilityScore(score).modifier()
            expect(modifier).toEqual(expectedModifier)
        }
    }
}