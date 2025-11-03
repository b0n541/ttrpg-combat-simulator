package net.b0n541.combatsimulator

import net.b0n541.combatsimulator.logic.RobotApi

class JsPlatform : Platform {
    override val name: String = "Web with Kotlin/JS"
}

actual fun getPlatform(): Platform = JsPlatform()

actual suspend fun executeRobotCode(code: String, game: RobotApi) {
    println("Not supported, yet.")
}