package net.b0n541.combatsimulator.logic

import org.jetbrains.compose.resources.DrawableResource

data class Position(val x: Int, val y: Int)

enum class CombatantType {
    PLAYER, ENEMY
}

enum class CombatOutcome {
    ONGOING, PLAYER_VICTORY, ENEMY_VICTORY, DRAW
}

sealed class Action {
    data class Move(val newPosition: Position) : Action()
    data class Attack(val target: Combatant) : Action()
    object Dodge : Action()
}

data class CombatState(
    val combatants: List<Combatant> = emptyList(),
    val currentTurnId: String? = null,
    val outcome: CombatOutcome = CombatOutcome.ONGOING,
    val lastAttackedTargetId: String? = null
)

data class Combatant(
    val name: String,
    val type: CombatantType,
    val maxHp: Int,
    val currentHp: Int = 0,
    val initiative: Int = 0,
    val attackPower: Int = 1,
    val imageResource: DrawableResource,
    val position: Position = Position(0, 0),
    val moveRange: Int = 3 // maximum squares per turn
) {
    val isAlive: Boolean
        get() = currentHp > 0

    fun rollInitiative(): Int = (1..20).random()
}