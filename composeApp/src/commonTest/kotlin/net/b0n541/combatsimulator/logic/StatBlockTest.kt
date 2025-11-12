package net.b0n541.combatsimulator.logic

import io.kotest.matchers.ints.shouldBeInRange
import io.kotest.matchers.shouldBe
import kotlin.random.Random
import kotlin.test.Test

class StatBlockTest {
    val statBlock = StatBlock(
        armorClass = 8,
        maxHitPoints = 15,
        speed = 20,
        initiative = -2,
        abilities = listOf(
            AbilityScore(Ability.STRENGTH, 10),
            AbilityScore(Ability.DEXTERITY, 13),
            AbilityScore(Ability.CONSTITUTION, 7),
            AbilityScore(Ability.INTELLIGENCE, 10, -2),
            AbilityScore(Ability.WISDOM, 13),
            AbilityScore(Ability.CHARISMA, 7, 2),
        )
    )

    @Test
    fun abilityRoll() {
        repeat(1000) {
            statBlock.abilityRoll(Ability.STRENGTH) shouldBeInRange 1..20
            statBlock.abilityRoll(Ability.DEXTERITY) shouldBeInRange 2..21
            statBlock.abilityRoll(Ability.CONSTITUTION) shouldBeInRange -1..18
        }
    }

    @Test
    fun savingThrowRoll() {
        repeat(1000) {
            statBlock.savingThrowRoll(Ability.INTELLIGENCE) shouldBeInRange -1..18
            statBlock.savingThrowRoll(Ability.WISDOM) shouldBeInRange 2..21
            statBlock.savingThrowRoll(Ability.CHARISMA) shouldBeInRange 3..22
        }
    }

    @Test
    fun missingAbility() {
        val statBlockWithoutAbilities = statBlock.copy(abilities = emptyList())

        repeat(1000) {
            statBlockWithoutAbilities.abilityRoll(Ability.STRENGTH) shouldBeInRange 1..20
            statBlockWithoutAbilities.savingThrowRoll(Ability.CHARISMA) shouldBeInRange 1..20
        }
    }

    @Test
    fun attackRollWithNatural1() {
        val natural1 = statBlock.copy(d20 = PredictableDice(1))

        repeat(1000) {
            natural1.attackRoll(Ability.WISDOM, 15) shouldBe AttackRollResult(1, hit = false, criticalHit = false)
        }
    }

    @Test
    fun attackRollWithNatural20() {
        val natural20 = statBlock.copy(d20 = PredictableDice(20))

        repeat(1000) {
            natural20.attackRoll(Ability.WISDOM, 15) shouldBe AttackRollResult(20, hit = true, criticalHit = true)
        }
    }

    @Test
    fun attackRollWithNoNatural1Or20() {
        val neverNatural20 = statBlock.copy(d20 = LimitedDice(2..19))

        repeat(1000) {
            val result = neverNatural20.attackRoll(Ability.STRENGTH, 2)
            println(result)
            result.value shouldBeInRange 2..19
            result.hit shouldBe true
            result.criticalHit shouldBe false
        }
    }
}

class PredictableDice(val expectedValue: Int) : Dice {
    override fun roll() = expectedValue
}

class LimitedDice(val range: IntRange) : Dice {
    override fun roll() = Random.nextInt(range.first, range.last + 1)
}