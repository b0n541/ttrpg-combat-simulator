package net.b0n541.combatsimulator

import net.b0n541.combatsimulator.logic.CombatController

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

expect suspend fun executeRobotCode(code: String, game: CombatController)