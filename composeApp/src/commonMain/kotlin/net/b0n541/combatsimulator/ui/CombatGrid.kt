package net.b0n541.combatsimulator.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import net.b0n541.combatsimulator.logic.*
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
    Res.drawable.dungeon_floor_16,
    Res.drawable.dungeon_floor_17,
    Res.drawable.dungeon_floor_18,
    Res.drawable.dungeon_floor_19,
    Res.drawable.dungeon_floor_20
)

private val wallTiles = listOf(
    Res.drawable.dungeon_wall_01,
    Res.drawable.dungeon_wall_02,
    Res.drawable.dungeon_wall_03,
    Res.drawable.dungeon_wall_04,
    Res.drawable.dungeon_wall_05
)

private val characterImages = mapOf(
    CharacterClass.BARBARIAN to Res.drawable.dwarf,
    CharacterClass.PALADIN to Res.drawable.paladin,
    CharacterClass.WIZARD to Res.drawable.wizard
)

private val monsterImages = mapOf(
    MonsterType.GOBLIN to Res.drawable.goblin,
    MonsterType.ORC to Res.drawable.orc,
    MonsterType.DRAGON to Res.drawable.dragon
)

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

    var draggedCombatant by remember { mutableStateOf<Combatant?>(null) }
    var dragPosition by remember { mutableStateOf<Offset?>(null) }
    var dropTarget by remember { mutableStateOf<Position?>(null) }
    var currentDraggedCombatantAvailableMoves by remember { mutableStateOf<List<Position>>(emptyList()) }
    val cellSize = 100.dp

    val combatantsByPosition = remember(combatState.combatants) {
        combatState.combatants.associateBy { it.position }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Turn menu
        val isCombatOngoing = combatState.outcome == CombatOutcome.ONGOING

        if (isCombatOngoing) {
            currentCombatant?.let {
                Text(text = "Current Turn: ${it.name}")
            }
        } else {
            Text(text = "Game over ... hit restart!")
        }

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.width((width * 100).dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    scope.launch {
                        draggedCombatant = null
                        controller.performAction(Action.Dodge)
                    }
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

        // Grid
        Box(
            modifier = Modifier
                .width((width * 100).dp)
                .pointerInput(currentCombatant, combatantsByPosition) {
                    detectDragGestures(
                        onDragStart = { startOffset ->
                            val x = (startOffset.x / cellSize.toPx()).toInt().coerceIn(0, width - 1)
                            val y = (startOffset.y / cellSize.toPx()).toInt().coerceIn(0, height - 1)
                            val startPos = Position(x, y)

                            if (currentCombatant?.position == startPos) {
                                draggedCombatant = currentCombatant
                                dragPosition = startOffset
                                currentDraggedCombatantAvailableMoves =
                                    controller.getAvailableMovePositions(currentCombatant)
                            }
                        },
                        onDrag = { change, dragAmount ->
                            draggedCombatant ?: return@detectDragGestures
                            dragPosition = (dragPosition ?: Offset.Zero) + dragAmount
                            val x = (dragPosition!!.x / cellSize.toPx()).toInt().coerceIn(0, width - 1)
                            val y = (dragPosition!!.y / cellSize.toPx()).toInt().coerceIn(0, height - 1)
                            val potentialDropTarget = Position(x, y)

                            val isOriginalPosition = potentialDropTarget == draggedCombatant!!.position
                            val isAvailableMove = currentDraggedCombatantAvailableMoves.contains(potentialDropTarget)
                            val targetCombatantAtPotentialDrop = combatantsByPosition[potentialDropTarget]
                            val isAvailableAttack = targetCombatantAtPotentialDrop != null &&
                                    targetCombatantAtPotentialDrop.isAlive &&
                                    targetCombatantAtPotentialDrop != draggedCombatant &&
                                    controller.isAttackValid(draggedCombatant!!, targetCombatantAtPotentialDrop)

                            if (isOriginalPosition || isAvailableMove || isAvailableAttack) {
                                dropTarget = potentialDropTarget
                            } else {
                                dropTarget = null
                            }

                            if (isAvailableMove || isAvailableAttack) {
                                controller.updateMovePath(potentialDropTarget)
                            } else {
                                draggedCombatant?.position?.let { controller.updateMovePath(it) }
                            }
                            change.consume()
                        },
                        onDragEnd = {
                            draggedCombatant?.let { attacker ->
                                dropTarget?.let { targetPos ->
                                    val targetCombatant = combatantsByPosition[targetPos]
                                    if (targetCombatant != null && targetCombatant.isAlive && targetCombatant != attacker && controller.isAttackValid(
                                            attacker,
                                            targetCombatant
                                        )
                                    ) {
                                        // Attack logic
                                        scope.launch {
                                            controller.performAction(Action.Attack(targetCombatant))
                                        }
                                    } else if (controller.isMoveValid(attacker, targetPos)) {
                                        // Move logic
                                        scope.launch { controller.performAction(Action.Move(targetPos)) }
                                    }
                                }
                            }
                            draggedCombatant?.position?.let { controller.updateMovePath(it) }
                            draggedCombatant = null
                            dragPosition = null
                            dropTarget = null
                            currentDraggedCombatantAvailableMoves = emptyList()
                        },
                        onDragCancel = {
                            draggedCombatant?.position?.let { controller.updateMovePath(it) }
                            draggedCombatant = null
                            dragPosition = null
                            dropTarget = null
                            currentDraggedCombatantAvailableMoves = emptyList()
                        },
                    )
                }
        ) {
            Column {
                for (y in 0 until height) {
                    GridRow(
                        y,
                        width,
                        combatantsByPosition,
                        currentCombatant,
                        scope,
                        controller,
                        draggedCombatant,
                        dropTarget,
                        currentDraggedCombatantAvailableMoves
                    )
                }
            }

            Canvas(modifier = Modifier.fillMaxSize()) {
                if (combatState.movePath.size > 1) {
                    val path = Path()
                    val pathPoints = combatState.movePath.map {
                        Offset(
                            x = (it.x * cellSize.toPx()) + cellSize.toPx() / 2,
                            y = (it.y * cellSize.toPx()) + cellSize.toPx() / 2
                        )
                    }
                    path.moveTo(pathPoints.first().x, pathPoints.first().y)
                    pathPoints.drop(1).forEach {
                        path.lineTo(it.x, it.y)
                    }
                    drawPath(
                        path = path,
                        color = Color.Yellow,
                        style = Stroke(width = 5.dp.toPx())
                    )
                }
            }

            DraggedCombatant(
                draggedCombatant = draggedCombatant,
                dragPosition = dragPosition,
                cellSize = cellSize
            )

            if (combatState.outcome != CombatOutcome.ONGOING) {
                val statusText = when (combatState.outcome) {
                    CombatOutcome.PLAYER_VICTORY -> "Players have won!"
                    CombatOutcome.MONSTER_VICTORY -> "Monsters have won!"
                    CombatOutcome.DRAW -> "The battle is a draw!"
                    else -> ""
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth()
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
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun GridRow(
    y: Int,
    width: Int,
    combatantsByPosition: Map<Position, Combatant>,
    currentCombatant: Combatant?,
    scope: kotlinx.coroutines.CoroutineScope,
    controller: CombatController,
    draggedCombatant: Combatant?,
    dropTarget: Position?,
    availableMovePositionsForDragged: List<Position>
) {
    Row {
        for (x in 0 until width) {
            val pos = Position(x, y)
            val cellCombatant = combatantsByPosition[pos]
            val isCurrentCombatant = currentCombatant != null && cellCombatant == currentCombatant

            val isBeingDragged = draggedCombatant == cellCombatant
            val isDropTarget = dropTarget == pos
            val isValidMoveTarget = draggedCombatant != null && availableMovePositionsForDragged.contains(pos)
            val isValidAttackTarget = draggedCombatant != null && cellCombatant != null &&
                    cellCombatant.isAlive && cellCombatant != draggedCombatant &&
                    controller.isAttackValid(draggedCombatant, cellCombatant)


            val overlayColor = when {
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
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Draw floor tile
                Image(
                    painter = painterResource(getFloorTileResource(x, y)),
                    contentDescription = "Floor tile",
                    modifier = Modifier.fillMaxSize()
                )

                // Highlight for valid move targets during drag
                if (isValidMoveTarget) {
                    Box(
                        modifier = Modifier.fillMaxSize().background(Color.Blue.copy(alpha = 0.3f))
                    )
                }

                // Highlight for valid attack targets during drag
                if (isValidAttackTarget) {
                    Box(
                        modifier = Modifier.fillMaxSize().background(Color.Red.copy(alpha = 0.3f))
                    )
                }

                // Highlight for the potential drop cell
                if (isDropTarget) {
                    val dropHighlightColor = when {
                        isValidMoveTarget -> Color.Green
                        isValidAttackTarget -> Color.Magenta
                        else -> Color.Red
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(dropHighlightColor.copy(alpha = 0.5f))
                    )
                }

                // Draw combatant if present
                cellCombatant?.let { combatant ->
                    // Hide original combatant while dragging
                    Box(Modifier.fillMaxSize().let { if (isBeingDragged) it.alpha(0f) else it }) {
                        val backgroundAlpha: Float
                        val imageColorFilter: ColorFilter?

                        if (combatant.isAlive) {
                            backgroundAlpha = if (isCurrentCombatant) 0.6f else 0.4f
                            imageColorFilter = null
                        } else {
                            backgroundAlpha = 0.7f // More opaque gray for defeated units
                            imageColorFilter =
                                ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) })
                        }

                        val combatantBackgroundColor = if (combatant.isAlive) overlayColor else Color.Gray
                        Image(
                            painter = painterResource(getCharacterImage(combatant)),
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

fun getCharacterImage(combatant: Combatant): DrawableResource {
    val image = if (combatant.party == CombatantParty.PLAYER)
        characterImages[combatant.characterClass]
    else monsterImages[combatant.monsterType]
    return image!!
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun DraggedCombatant(
    draggedCombatant: Combatant?,
    dragPosition: Offset?,
    cellSize: Dp
) {
    draggedCombatant?.let { combatant ->
        dragPosition?.let { position ->
            with(LocalDensity.current) {
                Box(
                    modifier = Modifier
                        .offset(
                            x = (position.x - cellSize.toPx() / 2).toDp(),
                            y = (position.y - cellSize.toPx() / 2).toDp()
                        )
                        .size(cellSize)
                ) {
                    Image(painterResource(getCharacterImage(combatant)), combatant.name)
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

    if (Level.isFloor(Position(x, y))) {
        val tileIndex = Random(seed).nextInt(0, floorTiles.size)
        return floorTiles[tileIndex]
    } else if (Level.isWall(Position(x, y))) {
        val tileIndex = Random(seed).nextInt(0, wallTiles.size)
        return wallTiles[tileIndex]
    }
    return floorTiles[0]
}
