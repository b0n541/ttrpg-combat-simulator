package net.b0n541.combatsimulator.logic

enum class Ability {
    STRENGTH, DEXTERITY, CONSTITUTION, INTELLIGENCE, WISDOM, CHARISMA;
}

data class AbilityScore(val ability: Ability, val score: Int) {
    /**
     * Calculates the ability modifier. The formula is (score - 10) / 2, rounded down.
     * Standard integer division truncates towards zero, which is incorrect for negative results.
     * For example, a score of 9 gives a modifier of -1. (9 - 10) / 2 = -0.5, which should round down to -1.
     * Integer division `-1 / 2` results in `0`.
     */
    fun modifier(): Int = if (score >= 10) (score - 10) / 2 else (score - 11) / 2
}
