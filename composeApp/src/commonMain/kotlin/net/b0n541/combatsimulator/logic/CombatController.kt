package net.b0n541.combatsimulator.logic

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.max

class CombatController {
    private val scope = CoroutineScope(Dispatchers.Default)
    val combatState: MutableStateFlow<CombatState> = MutableStateFlow(CombatState())

    fun addCombatant(combatant: Combatant) {
        combatState.update {
            it.copy(
                combatants = it.combatants + combatant.copy(position = getUnusedRandomPosition())
            )
        }
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

        printCombatants()
    }

    fun getCurrentCombatant(): Combatant? =
        combatState.value.combatants.firstOrNull { it.name == combatState.value.currentTurnId }

    fun updateMovePath(targetPosition: Position) {
        val current = getCurrentCombatant() ?: return
        if (current.position == targetPosition) {
            combatState.update { it.copy(movePath = emptyList()) }
            return
        }

        val path = findPath(current.position, targetPosition)

        val displayPath = path?.let {
            if (it.size - 1 > current.moveRange) {
                it.take(current.moveRange + 1)
            } else {
                it
            }
        } ?: emptyList()

        combatState.update { it.copy(movePath = displayPath) }
    }

    suspend fun performAction(action: Action) {
        val current = getCurrentCombatant() ?: return
        var turnEnded = false
        when (action) {
            is Action.Move -> moveCombatant(current, action.newPosition)

            is Action.Attack -> {
                attack(current, action.target)
                delay(500) // Wait for attack animation
            }

            Action.Dodge -> {
                println("${current.name} dodges")
                turnEnded = true
            } // do nothing
        }

        printCombatants()

        // Advance turn after any action
        if (turnEnded) nextTurn()
    }

    private fun printCombatants() {
        combatState.value.combatants.forEach { println(it) }
    }

    private fun moveCombatant(combatant: Combatant, newPosition: Position) {
        if (isMoveValid(combatant, newPosition)) {
            val distance = calculateDistance(combatant.position, newPosition)
            println("${combatant.name} moves $distance fields to $newPosition")
            combatState.update { state ->
                state.copy(
                    combatants = state.combatants.map { if (it.name == combatant.name) it.copy(position = newPosition) else it },
                    movePath = emptyList()
                )
            }
        } else {
            combatState.update { it.copy(movePath = emptyList()) }
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
                lastAttackedTargetId = defender.name,
                movePath = emptyList()
            )
        }

        scope.launch {
            delay(500) // Highlight duration
            combatState.update { it.copy(lastAttackedTargetId = null) }
        }
    }

    suspend fun nextTurn() {
        combatState.update { state ->
            val alive = state.combatants.filter { it.isAlive }

            val playersAlive = alive.any { it.party == CombatantParty.PLAYER }
            val monstersAlive = alive.any { it.party == CombatantParty.MONSTER }

            // End combat if no one is alive, or only one faction remains
            if (!playersAlive || !monstersAlive) {
                val newOutcome = when {
                    !playersAlive && !monstersAlive -> CombatOutcome.DRAW
                    !playersAlive -> CombatOutcome.MONSTER_VICTORY
                    else -> CombatOutcome.PLAYER_VICTORY
                }
                return@update state.copy(currentTurnId = null, outcome = newOutcome)
            }

            val currentIndex = alive.indexOfFirst { it.name == state.currentTurnId }
            val nextIndex = (currentIndex + 1) % alive.size
            state.copy(currentTurnId = alive[nextIndex].name)
        }
    }

    private fun chebyshevDistance(a: Position, b: Position) =
        max(abs(a.x - b.x), abs(a.y - b.y))

    private fun findPath(start: Position, end: Position): List<Position>? {
        val openSet = mutableSetOf(start)
        val cameFrom = mutableMapOf<Position, Position>()

        val gScore = mutableMapOf<Position, Int>().withDefault { Int.MAX_VALUE }
        gScore[start] = 0

        val fScore = mutableMapOf<Position, Int>().withDefault { Int.MAX_VALUE }
        fScore[start] = chebyshevDistance(start, end)

        while (openSet.isNotEmpty()) {
            val current = openSet.minByOrNull { fScore.getValue(it) }!!

            if (current == end) {
                val path = mutableListOf(current)
                var temp = current
                while (cameFrom.containsKey(temp)) {
                    temp = cameFrom.getValue(temp)
                    path.add(0, temp)
                }
                return path
            }

            openSet.remove(current)

            for (dx in -1..1) {
                for (dy in -1..1) {
                    if (dx == 0 && dy == 0) continue

                    val neighbor = Position(current.x + dx, current.y + dy)

                    if (!Level.isFloor(neighbor)) {
                        continue
                    }

                    val tentativeGScore = gScore.getValue(current) + 1

                    if (tentativeGScore < gScore.getValue(neighbor)) {
                        cameFrom[neighbor] = current
                        gScore[neighbor] = tentativeGScore
                        fScore[neighbor] = tentativeGScore + chebyshevDistance(neighbor, end)
                        if (neighbor !in openSet) {
                            openSet.add(neighbor)
                        }
                    }
                }
            }
        }

        return null // No path found
    }

    private fun calculateDistance(start: Position, end: Position): Int {
        if (start == end) return 0
        return findPath(start, end)?.size?.minus(1) ?: Int.MAX_VALUE
    }

    fun getAvailableMovePositions(combatant: Combatant): List<Position> {
        val positions = mutableListOf<Position>()
        for (y in Level.layout.indices) {
            for (x in Level.layout[y].indices) {
                val pos = Position(x, y)
                if (isMoveValid(combatant, pos)) {
                    positions.add(pos)
                }
            }
        }
        return positions
    }

    fun isMoveValid(combatant: Combatant, position: Position): Boolean {
        if (isFieldOccupied(position)) {
            println("Field $position is occupied")
            return false
        }

        val distance = calculateDistance(combatant.position, position)
        return distance in 1..combatant.moveRange
    }

    fun isFieldOccupied(position: Position): Boolean {
        if (!Level.isFloor(position)) {
            return true
        }
        return combatState.value.combatants.any { it.position == position && it.isAlive }
    }

    fun getUnusedRandomPosition(): Position {
        var position: Position
        do {
            var x = Level.layout.indices.random()
            var y = Level.layout[x].indices.random()
            position = Position(x, y)
        } while (isFieldOccupied(position))
        return position
    }

    fun isAttackValid(attacker: Combatant, target: Combatant): Boolean {
        val distance = calculateDistance(attacker.position, target.position)
        return distance <= attacker.moveRange
    }
}
