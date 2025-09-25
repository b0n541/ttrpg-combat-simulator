package net.b0n541.combatsimulator.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import net.b0n541.combatsimulator.logic.Action
import net.b0n541.combatsimulator.logic.CombatController
import net.b0n541.combatsimulator.logic.CombatOutcome
import net.b0n541.combatsimulator.logic.Position
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import ttrpg_combat_simulator.composeapp.generated.resources.*
import kotlin.random.Random

private val floorTiles = listOf(
    Res.drawable.dungeon_floor_01,
    Res.drawable.dungeon_floor_02,
    Res.drawable.dungeon_floor_03,
    Res.drawable.dungeon_floor_04,
    Res.drawable.dungeon_floor_05,
    Res.drawable.dungeon_floor_06,
    Res.drawable.dungeon_floor_07,
    Res.drawable.dungeon_floor_08,
    Res.drawable.dungeon_floor_09,
    Res.drawable.dungeon_floor_10,
    Res.drawable.dungeon_floor_11,
    Res.drawable.dungeon_floor_12,
    Res.drawable.dungeon_floor_13,
    Res.drawable.dungeon_floor_14,
    Res.drawable.dungeon_floor_15,
    Res.drawable.dungeon_floor_16
)

private sealed class SelectedAction {
    object Move : SelectedAction()
    object Attack : SelectedAction()
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun CombatGridView(
    controller: CombatController,
    width: Int,
    height: Int,
    onRestart: () -> Unit
) {
    val combatState by controller.combatState.collectAsState()
    val currentCombatant = combatState.combatants.firstOrNull { it.name == combatState.currentTurnId }
    val scope = rememberCoroutineScope()

    var selectedAction by remember { mutableStateOf<SelectedAction?>(null) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Turn menu
        val isCombatOngoing = combatState.outcome == CombatOutcome.ONGOING
        val statusText = when (combatState.outcome) {
            CombatOutcome.ONGOING -> currentCombatant?.let { "Current Turn: ${it.name}" } ?: ""
            CombatOutcome.PLAYER_VICTORY -> "Players have won!"
            CombatOutcome.ENEMY_VICTORY -> "Enemies have won!"
            CombatOutcome.DRAW -> "The battle is a draw!"
        }

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.width((width * 100).dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = { selectedAction = SelectedAction.Move }, enabled = isCombatOngoing) { Text("Move") }
            Spacer(Modifier.width(8.dp))
            Button(onClick = { selectedAction = SelectedAction.Attack }, enabled = isCombatOngoing) { Text("Attack") }
            Spacer(Modifier.width(8.dp))
            Button(
                onClick = {
                    scope.launch {
                        controller.performAction(Action.Dodge)
                    }
                    selectedAction = null
                },
                enabled = isCombatOngoing
            ) { Text("Dodge") }

            if (!isCombatOngoing) {
                Spacer(Modifier.weight(1f))
                Button(onClick = onRestart) {
                    Text("Start Again")
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Calculate available moves for highlighting
        val availableMoves = remember(selectedAction, combatState) {
            if (selectedAction == SelectedAction.Move && currentCombatant != null) {
                controller.getAvailableMovePositions(currentCombatant, width, height)
            } else emptyList()
        }

        // Grid
        Column {
            for (y in 0 until height) {
                GridRow(
                    y,
                    width,
                    combatState,
                    currentCombatant,
                    selectedAction,
                    availableMoves,
                    scope,
                    controller,
                    onAction = { selectedAction = null }
                )
            }
        }

        if (!isCombatOngoing) {
            Box(
                modifier = Modifier
                    .size((width * 100).dp, (height * 100).dp)
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = statusText,
                    color = Color.White,
                    fontSize = 50.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun GridRow(
    y: Int,
    width: Int,
    combatState: net.b0n541.combatsimulator.logic.CombatState,
    currentCombatant: net.b0n541.combatsimulator.logic.Combatant?,
    selectedAction: SelectedAction?,
    availableMoves: List<Position>,
    scope: kotlinx.coroutines.CoroutineScope,
    controller: CombatController,
    onAction: () -> Unit
) {
    Row {
        for (x in 0 until width) {
            val pos = Position(x, y)
            val cellCombatant = combatState.combatants.firstOrNull { it.position == pos }
            val isCurrentCombatant = currentCombatant != null && cellCombatant == currentCombatant
            val isAttacked = cellCombatant?.name == combatState.lastAttackedTargetId

            val overlayColor = when {
                isAttacked -> Color.Red
                isCurrentCombatant -> Color.Yellow
                else -> Color.Transparent
            }

            val borderColor = if (isCurrentCombatant) Color.Yellow else Color.Black
            val borderWidth = if (isCurrentCombatant) 3.dp else 1.dp

            Box(
                modifier = Modifier
                    .size(100.dp)
                    .border(
                        width = borderWidth,
                        color = borderColor
                    )
                    .clickable(enabled = currentCombatant != null) {
                        if (selectedAction == SelectedAction.Move && pos in availableMoves) {
                            scope.launch {
                                controller.performAction(Action.Move(pos))
                            }
                            onAction()
                        } else if (selectedAction == SelectedAction.Attack &&
                            cellCombatant != null &&
                            cellCombatant != currentCombatant &&
                            cellCombatant.isAlive
                        ) {
                            scope.launch {
                                controller.performAction(Action.Attack(cellCombatant))
                            }
                            onAction()
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                // Draw floor tile
                Image(
                    painter = painterResource(getFloorTileResource(x, y)),
                    contentDescription = "Floor tile",
                    modifier = Modifier.fillMaxSize()
                )

                // Draw highlight overlay for available moves
                if (pos in availableMoves) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Blue.copy(alpha = 0.3f))
                    )
                }

                // Draw combatant if present
                cellCombatant?.let { combatant ->
                    Box(Modifier.fillMaxSize()) {
                        val backgroundAlpha: Float
                        val imageColorFilter: ColorFilter?

                        if (combatant.isAlive) {
                            backgroundAlpha = if (isCurrentCombatant || isAttacked) 0.6f else 0.4f
                            imageColorFilter = null
                        } else {
                            backgroundAlpha = 0.7f // More opaque gray for defeated units
                            imageColorFilter =
                                ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) })
                        }

                        val combatantBackgroundColor = if (combatant.isAlive) overlayColor else Color.Gray

                        Image(
                            painter = painterResource(combatant.imageResource),
                            contentDescription = combatant.name,
                            colorFilter = imageColorFilter,
                            modifier = Modifier
                                .fillMaxSize()
                                .background(combatantBackgroundColor.copy(alpha = backgroundAlpha))
                                .padding(4.dp)
                        )

                        if (combatant.isAlive) {
                            HealthBar(
                                currentHp = combatant.currentHp,
                                maxHp = combatant.maxHp,
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(horizontal = 8.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HealthBar(currentHp: Int, maxHp: Int, modifier: Modifier = Modifier) {
    val healthPercentage = (currentHp.toFloat() / maxHp.toFloat()).coerceIn(0f, 1f)
    val healthBarColor = when {
        healthPercentage > 0.5f -> Color.Green
        healthPercentage > 0.2f -> Color.Yellow
        else -> Color.Red
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(8.dp)
            .background(Color.Gray.copy(alpha = 0.8f))
            .border(1.dp, Color.Black.copy(alpha = 0.8f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(healthPercentage)
                .fillMaxHeight()
                .background(healthBarColor)
        )
    }
}

private fun getFloorTileResource(x: Int, y: Int): DrawableResource {
    val seed = x * 1000 + y // Simple seed from coordinates
    val tileIndex = Random(seed).nextInt(0, floorTiles.size)
    return floorTiles[tileIndex]
}
