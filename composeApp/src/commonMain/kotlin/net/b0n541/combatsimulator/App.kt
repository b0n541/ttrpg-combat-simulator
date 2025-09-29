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
import net.b0n541.combatsimulator.ui.DarkColorScheme
import net.b0n541.combatsimulator.ui.getTypography
import org.jetbrains.compose.ui.tooling.preview.Preview

private const val GRID_WIDTH = 10
private const val GRID_HEIGHT = 10


@Composable
@Preview
fun App() {
    MaterialTheme(colorScheme = DarkColorScheme, typography = getTypography()) {
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
    mutableSetOf<Position>()

    controller.resetCombatants()

    controller.addCombatant(
        Combatant(
            "Dwarf",
            CharacterClass.BARBARIAN,
            null,
            CombatantParty.PLAYER,
            maxHp = 10,
            attackPower = 3
        )
    )
    controller.addCombatant(
        Combatant(
            "Wizard",
            CharacterClass.WIZARD,
            null,
            CombatantParty.PLAYER,
            maxHp = 5,
            attackPower = 1
        )
    )
    controller.addCombatant(
        Combatant(
            "Paladin",
            CharacterClass.PALADIN,
            null,
            CombatantParty.PLAYER,
            maxHp = 5,
            attackPower = 1
        )
    )
    controller.addCombatant(
        Combatant(
            "Orc",
            null,
            MonsterType.ORC,
            CombatantParty.MONSTER,
            maxHp = 8,
            attackPower = 2
        )
    )
    controller.addCombatant(
        Combatant(
            "Goblin",
            null,
            MonsterType.GOBLIN,
            CombatantParty.MONSTER,
            maxHp = 5,
            attackPower = 1
        )
    )
    controller.addCombatant(
        Combatant(
            "Dragon",
            null,
            MonsterType.DRAGON,
            CombatantParty.MONSTER,
            maxHp = 5,
            attackPower = 1
        )
    )

    controller.startCombat()
}
