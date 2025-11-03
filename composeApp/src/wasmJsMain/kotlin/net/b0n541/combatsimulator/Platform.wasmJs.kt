package net.b0n541.combatsimulator

import net.b0n541.combatsimulator.logic.CombatController

class WasmPlatform : Platform {
    override val name: String = "Web with Kotlin/Wasm"
}

actual fun getPlatform(): Platform = WasmPlatform()

actual suspend fun executeRobotCode(code: String, game: CombatController) {
    println("Not supported, yet.")
}