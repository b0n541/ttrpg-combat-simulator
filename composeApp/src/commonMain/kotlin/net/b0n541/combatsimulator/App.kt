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
import net.b0n541.combatsimulator.logic.CombatController
import net.b0n541.combatsimulator.logic.Combatant
import net.b0n541.combatsimulator.logic.CombatantType
import net.b0n541.combatsimulator.logic.Position
import net.b0n541.combatsimulator.ui.CombatGridView
import org.jetbrains.compose.ui.tooling.preview.Preview
import ttrpg_combat_simulator.composeapp.generated.resources.*


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
            val startNewGame = { startNewGame(controller) }

            // Initialize combatants
            LaunchedEffect(Unit) {
                startNewGame()
            }

            CombatGridView(
                controller,
                width = 10,
                height = 10,
                onRestart = startNewGame
            )

            Spacer(Modifier.height(20.dp))
        }
    }
}

private fun startNewGame(controller: CombatController) {
    val gridWidth = 10
    val gridHeight = 10
    val usedPositions = mutableSetOf<Position>()

    controller.resetCombatants()

    controller.addCombatant(
        Combatant(
            "Dwarf",
            CombatantType.PLAYER,
            maxHp = 10,
            attackPower = 3,
            position = getUnusedRandomPosition(gridWidth, gridHeight, usedPositions),
            imageResource = Res.drawable.dwarf
        )
    )
    controller.addCombatant(
        Combatant(
            "Orc",
            CombatantType.ENEMY,
            maxHp = 8,
            attackPower = 2,
            position = getUnusedRandomPosition(gridWidth, gridHeight, usedPositions),
            imageResource = Res.drawable.orc
        )
    )
    controller.addCombatant(
        Combatant(
            "Goblin",
            CombatantType.ENEMY,
            maxHp = 5,
            attackPower = 1,
            position = getUnusedRandomPosition(gridWidth, gridHeight, usedPositions),
            imageResource = Res.drawable.goblin
        )
    )
    controller.addCombatant(
        Combatant(
            "Wizard",
            CombatantType.PLAYER,
            maxHp = 5,
            attackPower = 1,
            position = getUnusedRandomPosition(gridWidth, gridHeight, usedPositions),
            imageResource = Res.drawable.wizard
        )
    )
    controller.addCombatant(
        Combatant(
            "Paladin",
            CombatantType.PLAYER,
            maxHp = 5,
            attackPower = 1,
            position = getUnusedRandomPosition(gridWidth, gridHeight, usedPositions),
            imageResource = Res.drawable.paladin
        )
    )
    controller.addCombatant(
        Combatant(
            "Dragon",
            CombatantType.ENEMY,
            maxHp = 5,
            attackPower = 1,
            position = getUnusedRandomPosition(gridWidth, gridHeight, usedPositions),
            imageResource = Res.drawable.dragon
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
