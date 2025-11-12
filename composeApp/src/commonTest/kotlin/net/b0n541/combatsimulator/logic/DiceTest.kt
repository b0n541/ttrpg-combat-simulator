package net.b0n541.combatsimulator.logic

import io.kotest.matchers.ints.shouldBeInRange
import kotlin.test.Test

class DiceTest {
    @Test
    fun roll() {
        repeat(1000) {
            StandardDice.D4.roll() shouldBeInRange 1..4
            StandardDice.D6.roll() shouldBeInRange 1..6
            StandardDice.D8.roll() shouldBeInRange 1..8
            StandardDice.D10.roll() shouldBeInRange 1..10
            StandardDice.D12.roll() shouldBeInRange 1..12
            StandardDice.D20.roll() shouldBeInRange 1..20
            StandardDice.D100.roll() shouldBeInRange 1..100
        }
    }
}