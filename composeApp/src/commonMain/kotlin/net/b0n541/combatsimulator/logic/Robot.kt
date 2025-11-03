package net.b0n541.combatsimulator.logic

import kotlinx.coroutines.delay

enum class Direction { N, NE, E, SE, S, SW, W, NW }

class Robot(private val controller: CombatController) {

    suspend fun move(direction: Direction) {
        val currentPosition = controller.getCurrentCombatant()?.position
        if (currentPosition != null) {
            val (dx, dy) = directionVector(direction)
            val nextPosition = Position(currentPosition.x + dx, currentPosition.y + dy)
            if (isMovePossible(nextPosition)) {
                controller.performAction(Action.Move(nextPosition))
                delay(400)
            }
        }
    }

    private fun getNextPosition(): Position? {
        val (dx, dy) = directionVector(Direction.N)
        val currentPosition = controller.getCurrentCombatant()?.position
        if (currentPosition != null) {
            return Position(currentPosition.x + dx, currentPosition.y + dy)
        }
        return null
    }

    private fun isMovePossible(nextPosition: Position): Boolean {
        return controller.isMoveValid(controller.getCurrentCombatant()!!, nextPosition)
    }

    suspend fun attack(direction: Direction) {
        val currentPosition = controller.getCurrentCombatant()?.position
        if (currentPosition != null) {
            val (dx, dy) = directionVector(direction)
            val nextPosition = Position(currentPosition.x + dx, currentPosition.y + dy)
            val opponent = controller.getOpponentByPosition(nextPosition)

            if (opponent == null) {
                println("There is nobody...")
                return
            }

            if (controller.isAttackValid(controller.getCurrentCombatant()!!, opponent)) {
                controller.performAction(Action.Attack(opponent))
                delay(400)
            } else {
                println("Can't attack there")
            }
        }
    }

    fun nextTurn() {
        controller.nextTurn()
    }

    private fun directionVector(direction: Direction): Pair<Int, Int> = when (direction) {
        Direction.N -> 0 to -1
        Direction.NE -> 1 to -1
        Direction.E -> 1 to 0
        Direction.SE -> 1 to 1
        Direction.S -> 0 to 1
        Direction.SW -> -1 to 1
        Direction.W -> -1 to 0
        Direction.NW -> -1 to -1
    }
}

suspend fun robot(game: RobotApi, block: suspend Robot.() -> Unit) {
    val controller = game as CombatController
    val robot = Robot(controller)
    robot.block()
}