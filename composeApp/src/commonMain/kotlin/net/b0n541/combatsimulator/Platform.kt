package net.b0n541.combatsimulator

import net.b0n541.combatsimulator.logic.RobotApi

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

expect suspend fun executeRobotCode(code: String, game: RobotApi)