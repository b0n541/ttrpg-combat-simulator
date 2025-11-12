package net.b0n541.combatsimulator.logic

import kotlin.random.Random

interface Dice {
    fun roll(): Int
}

enum class StandardDice(val sides: Int) : Dice {
    D4(4),
    D6(6),
    D8(8),
    D10(10),
    D12(12),
    D20(20),
    D100(100);

    override fun roll() = Random.nextInt(1, sides + 1)
}
