package net.b0n541.combatsimulator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.b0n541.combatsimulator.logic.*
import net.b0n541.combatsimulator.ui.CombatGridView
import org.jetbrains.compose.ui.tooling.preview.Preview

private const val GRID_WIDTH = 10
private const val GRID_HEIGHT = 10


@Composable
@Preview
fun App() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .safeContentPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val controller = remember { CombatController() }
            val startNewGame = { startNewGame(controller, GRID_WIDTH, GRID_HEIGHT) }

            // Initialize combatants
            LaunchedEffect(Unit) {
                startNewGame()
            }

            CombatGridView(
                controller = controller,
                width = GRID_WIDTH,
                height = GRID_HEIGHT,
                onRestart = startNewGame
            )

            Spacer(Modifier.height(20.dp))
        }
    }
}

private fun startNewGame(controller: CombatController, gridWidth: Int, gridHeight: Int) {
    val usedPositions = mutableSetOf<Position>()

    controller.resetCombatants()

    controller.addCombatant(
        Combatant(
            "Dwarf",
            CharacterClass.BARBARIAN,
            null,
            CombatantParty.PLAYER,
            maxHp = 10,
            attackPower = 3,
            position = getUnusedRandomPosition(gridWidth, gridHeight, usedPositions)
        )
    )
    controller.addCombatant(
        Combatant(
            "Wizard",
            CharacterClass.WIZARD,
            null,
            CombatantParty.PLAYER,
            maxHp = 5,
            attackPower = 1,
            position = getUnusedRandomPosition(gridWidth, gridHeight, usedPositions)
        )
    )
    controller.addCombatant(
        Combatant(
            "Paladin",
            CharacterClass.PALADIN,
            null,
            CombatantParty.PLAYER,
            maxHp = 5,
            attackPower = 1,
            position = getUnusedRandomPosition(gridWidth, gridHeight, usedPositions)
        )
    )
    controller.addCombatant(
        Combatant(
            "Orc",
            null,
            MonsterType.ORC,
            CombatantParty.MONSTER,
            maxHp = 8,
            attackPower = 2,
            position = getUnusedRandomPosition(gridWidth, gridHeight, usedPositions)
        )
    )
    controller.addCombatant(
        Combatant(
            "Goblin",
            null,
            MonsterType.GOBLIN,
            CombatantParty.MONSTER,
            maxHp = 5,
            attackPower = 1,
            position = getUnusedRandomPosition(gridWidth, gridHeight, usedPositions)
        )
    )
    controller.addCombatant(
        Combatant(
            "Dragon",
            null,
            MonsterType.DRAGON,
            CombatantParty.MONSTER,
            maxHp = 5,
            attackPower = 1,
            position = getUnusedRandomPosition(gridWidth, gridHeight, usedPositions)
        )
    )

    controller.startCombat()
}

private fun getUnusedRandomPosition(
    gridWidth: Int,
    gridHeight: Int,
    usedPositions: MutableSet<Position>
): Position {
    var position: Position
    do {
        position = Position((0 until gridWidth).random(), (0 until gridHeight).random())
    } while (position in usedPositions)
    usedPositions.add(position)
    return position
}
