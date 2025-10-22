package net.b0n541.combatsimulator.logic

import kotlinx.coroutines.delay

enum class Direction { UP, RIGHT, DOWN, LEFT }

class Robot(private val controller: CombatController) {
    private var direction = Direction.UP

    suspend fun moveForward(steps: Int = 1) {
        repeat(steps) {
            val (dx, dy) = directionVector()
            val currentPosition = controller.getCurrentCombatant()?.position
            if (currentPosition != null) {
                val nextPosition = Position(currentPosition.x + dx, currentPosition.y + dy)
                if (canMoveForward(nextPosition)) {
                    controller.performAction(Action.Move(nextPosition))
                }
                delay(400)
            }
        }
    }

    fun canMoveForward(nextPosition: Position): Boolean {
        return controller.isMoveValid(controller.getCurrentCombatant()!!, nextPosition)
    }

    suspend fun turnLeft() {
        direction = when (direction) {
            Direction.UP -> Direction.LEFT
            Direction.LEFT -> Direction.DOWN
            Direction.DOWN -> Direction.RIGHT
            Direction.RIGHT -> Direction.UP
        }
        delay(200)
    }

    suspend fun turnRight() {
        direction = when (direction) {
            Direction.UP -> Direction.RIGHT
            Direction.RIGHT -> Direction.DOWN
            Direction.DOWN -> Direction.LEFT
            Direction.LEFT -> Direction.UP
        }
        delay(200)
    }

    private fun directionVector(): Pair<Int, Int> = when (direction) {
        Direction.UP -> 0 to -1
        Direction.RIGHT -> 1 to 0
        Direction.DOWN -> 0 to 1
        Direction.LEFT -> -1 to 0
    }
}

suspend fun robot(controller: CombatController, block: suspend Robot.() -> Unit) {
    val r = Robot(controller)
    r.block()
}