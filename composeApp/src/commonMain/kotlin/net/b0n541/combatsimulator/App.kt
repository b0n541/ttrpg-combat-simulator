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

            // Initialize combatants
            LaunchedEffect(Unit) {
                controller.addCombatant(
                    Combatant(
                        "Dwarf",
                        CombatantType.PLAYER,
                        maxHp = 10,
                        attackPower = 3,
                        position = Position(3, 3),
                        imageResource = Res.drawable.dwarf
                    )
                )
                controller.addCombatant(
                    Combatant(
                        "Orc",
                        CombatantType.ENEMY,
                        maxHp = 8,
                        attackPower = 2,
                        position = Position(5, 3),
                        imageResource = Res.drawable.orc
                    )
                )
                controller.addCombatant(
                    Combatant(
                        "Goblin",
                        CombatantType.ENEMY,
                        maxHp = 5,
                        attackPower = 1,
                        position = Position(4, 5),
                        imageResource = Res.drawable.goblin
                    )
                )
                controller.addCombatant(
                    Combatant(
                        "Wizard",
                        CombatantType.PLAYER,
                        maxHp = 5,
                        attackPower = 1,
                        position = Position(6, 5),
                        imageResource = Res.drawable.wizard
                    )
                )
                controller.addCombatant(
                    Combatant(
                        "Paladin",
                        CombatantType.PLAYER,
                        maxHp = 5,
                        attackPower = 1,
                        position = Position(5, 7),
                        imageResource = Res.drawable.paladin
                    )
                )
                controller.addCombatant(
                    Combatant(
                        "Dragon",
                        CombatantType.ENEMY,
                        maxHp = 5,
                        attackPower = 1,
                        position = Position(8, 2),
                        imageResource = Res.drawable.dragon
                    )
                )

                controller.startCombat()
            }

            CombatGridView(controller, width = 10, height = 10)

            Spacer(Modifier.height(20.dp))
        }
    }
}
