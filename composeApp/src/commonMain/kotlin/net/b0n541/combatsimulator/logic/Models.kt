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

enum class CharacterSpecies {
    DRAGONBORN, DWARF, ELF, GNOME, GOLIATH, HALFLING, HUMAN, ORC, TIEFLING
}

enum class MonsterType {
    DRAGON, GOBLIN, ANKHEG, ZOMBIE
}

data class Combatant(
    val name: String,
    val characterClass: CharacterClass? = null,
    val characterSpecies: CharacterSpecies? = null,
    val monsterType: MonsterType? = null,
    val party: CombatantParty,
    val maxHp: Int = 0,
    val currentHp: Int = maxHp,
    val initiative: Int = 0,
    val attackPower: Int = 1,
    val position: Position = Position(0, 0),
    val moveRange: Int = 3, // maximum squares per turn
    val statBlock: StatBlock? = null
) {
    val isAlive = currentHp > 0

    fun rollInitiative() = (1..20).random()

    override fun toString(): String {
        val characterClass = if (party == CombatantParty.PLAYER) characterClass else monsterType
        return "Combatant(name: $name, class: $characterClass, party: $party, initiative: $initiative, range: $moveRange, hit points: $currentHp/$maxHp attack: $attackPower at: $position)"
    }
}

object PlayerParty {
    val creatures = mapOf(
        "Dwarf 1" to Combatant(
            "Dwarf 1", CharacterClass.BARBARIAN, CharacterSpecies.DWARF, null,
            CombatantParty.PLAYER,
            maxHp = 10, attackPower = 3,
            statBlock = StatBlock(
                18, 11, 25, -1,
                listOf(
                    AbilityScore(Ability.STRENGTH, 14),
                    AbilityScore(Ability.DEXTERITY, 8),
                    AbilityScore(Ability.CONSTITUTION, 15),
                    AbilityScore(Ability.INTELLIGENCE, 10),
                    AbilityScore(Ability.WISDOM, 16),
                    AbilityScore(Ability.CHARISMA, 12)
                )
            )
        ),
        "Wizard 1" to Combatant(
            "Wizard 1", CharacterClass.WIZARD, CharacterSpecies.ELF, null,
            CombatantParty.PLAYER,
            maxHp = 5, attackPower = 1,
            statBlock = StatBlock(
                12, 8, 30, 2,
                listOf(
                    AbilityScore(Ability.STRENGTH, 10),
                    AbilityScore(Ability.DEXTERITY, 14),
                    AbilityScore(Ability.CONSTITUTION, 15),
                    AbilityScore(Ability.INTELLIGENCE, 16),
                    AbilityScore(Ability.WISDOM, 12),
                    AbilityScore(Ability.CHARISMA, 8)
                )
            )
        ),
        "Paladin 1" to Combatant(
            "Paladin 1", CharacterClass.PALADIN, CharacterSpecies.HUMAN, null,
            CombatantParty.PLAYER,
            maxHp = 5, attackPower = 1,
            statBlock = StatBlock(
                18, 12, 30, -1,
                listOf(
                    AbilityScore(Ability.STRENGTH, 16),
                    AbilityScore(Ability.DEXTERITY, 9),
                    AbilityScore(Ability.CONSTITUTION, 15),
                    AbilityScore(Ability.INTELLIGENCE, 11),
                    AbilityScore(Ability.WISDOM, 13),
                    AbilityScore(Ability.CHARISMA, 14)
                )
            )
        )
    )
}

object MonsterParty {
    val monsters = mapOf(
        "Ankheg 1" to Combatant(
            "Ankheg 1", null, null, MonsterType.ANKHEG,
            CombatantParty.MONSTER,
            maxHp = 8, attackPower = 2,
            statBlock = StatBlock(
                14, 45, 30, 0,
                listOf(
                    AbilityScore(Ability.STRENGTH, 17),
                    AbilityScore(Ability.DEXTERITY, 11),
                    AbilityScore(Ability.CONSTITUTION, 14),
                    AbilityScore(Ability.INTELLIGENCE, 1),
                    AbilityScore(Ability.WISDOM, 13),
                    AbilityScore(Ability.CHARISMA, 6)
                )
            )
        ),
        "Goblin 1" to Combatant(
            "Goblin 1", null, null, MonsterType.GOBLIN,
            CombatantParty.MONSTER,
            maxHp = 5, attackPower = 1,
            statBlock = StatBlock(
                15, 10, 30, 2,
                listOf(
                    AbilityScore(Ability.STRENGTH, 8),
                    AbilityScore(Ability.DEXTERITY, 15),
                    AbilityScore(Ability.CONSTITUTION, 10),
                    AbilityScore(Ability.INTELLIGENCE, 10),
                    AbilityScore(Ability.WISDOM, 8),
                    AbilityScore(Ability.CHARISMA, 8)
                )
            )
        ),
        "Dragon 1" to Combatant(
            "Dragon 1", null, null, MonsterType.DRAGON,
            CombatantParty.MONSTER,
            maxHp = 5, attackPower = 1,
            statBlock = StatBlock(
                19, 256, 40, +12,
                listOf(
                    AbilityScore(Ability.STRENGTH, 27),
                    AbilityScore(Ability.DEXTERITY, 10, 6),
                    AbilityScore(Ability.CONSTITUTION, 25),
                    AbilityScore(Ability.INTELLIGENCE, 16),
                    AbilityScore(Ability.WISDOM, 13, 7),
                    AbilityScore(Ability.CHARISMA, 23)
                )
            )
        ),
        "Zombie 1" to Combatant(
            "Zombie 1", null, null, MonsterType.ZOMBIE,
            CombatantParty.MONSTER,
            maxHp = 5, attackPower = 1,
            statBlock = StatBlock(
                8, 15, 20, -2,
                listOf(
                    AbilityScore(Ability.STRENGTH, 13),
                    AbilityScore(Ability.DEXTERITY, 6),
                    AbilityScore(Ability.CONSTITUTION, 16),
                    AbilityScore(Ability.INTELLIGENCE, 3),
                    AbilityScore(Ability.WISDOM, 6, 0),
                    AbilityScore(Ability.CHARISMA, 5),
                )
            )
        )
    )
}
