package net.b0n541.combatsimulator.logic

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CombatController {

    private val scope = CoroutineScope(Dispatchers.Default)
    val combatState: MutableStateFlow<CombatState> = MutableStateFlow(CombatState())

    fun addCombatant(combatant: Combatant) {
        combatState.update { it.copy(combatants = it.combatants + combatant) }
    }

    fun resetCombatants() {
        combatState.update { it.copy(combatants = emptyList()) }
    }

    fun startCombat() {
        combatState.update { state ->
            val startedCombatants = state.combatants
                .map { it.copy(currentHp = it.maxHp, initiative = it.rollInitiative()) }
                .sortedByDescending { it.initiative }
            state.copy(
                combatants = startedCombatants,
                currentTurnId = startedCombatants.firstOrNull()?.name,
                outcome = CombatOutcome.ONGOING
            )
        }
    }

    fun getCurrentCombatant(): Combatant? =
        combatState.value.combatants.firstOrNull { it.name == combatState.value.currentTurnId }

    suspend fun performAction(action: Action) {
        val current = getCurrentCombatant() ?: return
        var turnEnded = true
        when (action) {
            is Action.Move -> moveCombatant(current, action.newPosition)
            is Action.Attack -> {
                attack(current, action.target)
                delay(500) // Wait for attack animation
            }
            Action.Dodge -> println("${current.name} dodges") // do nothing
        }

        combatState.value.combatants.forEach { println(it) }

        // Advance turn after any action
        if (turnEnded) nextTurn()
    }

    private fun moveCombatant(combatant: Combatant, newPosition: Position) {
        // Ensure the target cell is empty
        val occupied = combatState.value.combatants.any { it.position == newPosition && it.isAlive }
        val distance = manhattanDistance(combatant.position, newPosition)
        if (!occupied && distance <= combatant.moveRange) {
            println("${combatant.name} moves $distance fields to $newPosition")
            combatState.update { state ->
                state.copy(combatants = state.combatants.map { if (it.name == combatant.name) it.copy(position = newPosition) else it })
            }
        }
    }

    private fun attack(attacker: Combatant, defender: Combatant) {
        if (!attacker.isAlive || !defender.isAlive) return

        println("${attacker.name} attacks ${defender.name} with attack power ${attacker.attackPower}")

        combatState.update { state ->
            state.copy(
                combatants = state.combatants.map {
                    if (it.name == defender.name) {
                        it.copy(currentHp = (it.currentHp - attacker.attackPower).coerceAtLeast(0))
                    } else it
                },
                lastAttackedTargetId = defender.name
            )
        }

        scope.launch {
            delay(500) // Highlight duration
            combatState.update { it.copy(lastAttackedTargetId = null) }
        }
    }

    private fun nextTurn() {
        combatState.update { state ->
            val alive = state.combatants.filter { it.isAlive }

            val playersAlive = alive.any { it.type == CombatantType.PLAYER }
            val enemiesAlive = alive.any { it.type == CombatantType.ENEMY }

            // End combat if no one is alive, or only one faction remains
            if (!playersAlive || !enemiesAlive) {
                val newOutcome = when {
                    !playersAlive && !enemiesAlive -> CombatOutcome.DRAW
                    !playersAlive -> CombatOutcome.ENEMY_VICTORY
                    else -> CombatOutcome.PLAYER_VICTORY
                }
                return@update state.copy(currentTurnId = null, outcome = newOutcome)
            }

            val currentIndex = alive.indexOfFirst { it.name == state.currentTurnId }
            val nextIndex = (currentIndex + 1) % alive.size
            state.copy(currentTurnId = alive[nextIndex].name)
        }
    }

    private fun manhattanDistance(a: Position, b: Position) =
        kotlin.math.abs(a.x - b.x) + kotlin.math.abs(a.y - b.y)

    fun getAvailableMovePositions(combatant: Combatant, width: Int, height: Int): List<Position> {
        val positions = mutableListOf<Position>()
        for (x in 0 until width) {
            for (y in 0 until height) {
                val pos = Position(x, y)
                val distance = manhattanDistance(combatant.position, pos)
                val occupied = combatState.value.combatants.any { it.position == pos && it.isAlive }
                if (distance in 1..combatant.moveRange && !occupied) {
                    positions.add(pos)
                }
            }
        }
        return positions
    }
}
