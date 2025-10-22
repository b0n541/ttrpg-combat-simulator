package net.b0n541.combatsimulator.logic

object Level {
    val layout = arrayOf(
        "  WWWWWW  ",
        "WWW....WWW",
        "W........W",
        "W...WW...W",
        "W........W",
        "W........W",
        "W...WW...W",
        "W........W",
        "WWW....WWW",
        "  WWWWWW  "
    )

    fun isFloor(position: Position): Boolean {
        if (position.x < 0 || position.x >= layout[0].length || position.y < 0 || position.y >= layout.size) {
            return false
        }
        return layout[position.y][position.x] == '.'
    }

    fun isWall(position: Position): Boolean {
        if (position.x < 0 || position.x >= layout[0].length || position.y < 0 || position.y >= layout.size) {
            return false
        }
        return layout[position.y][position.x] == 'W'
    }
}

data class Position(val x: Int, val y: Int)

enum class CombatantParty {
    PLAYER, MONSTER
}

enum class CombatOutcome {
    ONGOING, PLAYER_VICTORY, MONSTER_VICTORY, DRAW
}

sealed class Action {
    data class Move(val newPosition: Position) : Action()
    data class Attack(val target: Combatant) : Action()
    object Dodge : Action()
}

data class CombatState(
    val combatants: List<Combatant> = emptyList(),
    val currentTurnId: String? = null,
    val outcome: CombatOutcome = CombatOutcome.ONGOING,
    val lastAttackedTargetId: String? = null,
    val movePath: List<Position> = emptyList()
)

enum class CharacterClass {
    BARBARIAN, BARD, CLERIC, DRUID, FIGHTER, MONK, PALADIN, RANGER, ROGUE, SORCERER, WARLOCK, WIZARD
}

enum class MonsterType {
    GOBLIN, ORC, DRAGON
}

data class Combatant(
    val name: String,
    val characterClass: CharacterClass? = null,
    val monsterType: MonsterType? = null,
    val party: CombatantParty,
    val maxHp: Int,
    val currentHp: Int = maxHp,
    val initiative: Int = 0,
    val attackPower: Int = 1,
    val position: Position = Position(0, 0),
    val moveRange: Int = 3, // maximum squares per turn
    val abilities: Map<Ability, Int> = emptyMap()
) {
    val isAlive: Boolean
        get() = currentHp > 0

    fun rollInitiative(): Int = (1..20).random()

    override fun toString(): String {
        val characterClass = if (party == CombatantParty.PLAYER) characterClass else monsterType
        return "Combatant(name: $name, class: $characterClass, party: $party, initiative: $initiative, range: $moveRange, hit points: $currentHp/$maxHp attack: $attackPower at: $position)"
    }
}