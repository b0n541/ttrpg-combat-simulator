package net.b0n541.combatsimulator

import net.b0n541.combatsimulator.logic.RobotApi

class WasmPlatform : Platform {
    override val name: String = "Web with Kotlin/Wasm"
}

actual fun getPlatform(): Platform = WasmPlatform()

actual suspend fun executeRobotCode(code: String, game: RobotApi) {
    println("Not supported, yet.")
}