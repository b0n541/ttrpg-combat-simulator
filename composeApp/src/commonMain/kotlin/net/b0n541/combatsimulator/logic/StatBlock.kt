package net.b0n541.combatsimulator.logic

/**
 * Stat block for a creature.
 *
 * @property armorClass Armor class
 * @property maxHitPoints Maximum hit points
 * @property speed Speed in feet
 * @property initiative Initiative bonus
 * @property abilities List of abilities
 */
// TODO: support standard array (15, 14, 13, 12, 10, 8), random generation or point cost based ability scores
data class StatBlock(
    val armorClass: Int,
    val maxHitPoints: Int,
    val speed: Int,
    val initiative: Int,
    val abilities: List<AbilityScore>,
    val d20: Dice = StandardDice.D20
) {
    private val abilityMap = abilities.associateBy { it.ability }

    fun abilityRoll(ability: Ability): Int = d20.roll() + (abilityMap[ability]?.abilityModifier() ?: 0)

    fun attackRoll(ability: Ability, enemyArmorClass: Int): AttackRollResult {
        return when (val d20Value = d20.roll()) {
            1 -> AttackRollResult(d20Value, hit = false, criticalHit = false)
            20 -> AttackRollResult(d20Value, hit = true, criticalHit = true)
            else -> {
                val modifiedD20Valued20Value = d20Value + (abilityMap[ability]?.abilityModifier() ?: 0)
                return AttackRollResult(modifiedD20Valued20Value, modifiedD20Valued20Value >= enemyArmorClass, false)
            }
        }
    }

    fun savingThrowRoll(ability: Ability): Int = d20.roll() + (abilityMap[ability]?.savingThrowModifier() ?: 0)
}

data class AttackRollResult(val value: Int, val hit: Boolean, val criticalHit: Boolean)
