package net.b0n541.combatsimulator

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.b0n541.combatsimulator.logic.RobotApi
import net.b0n541.combatsimulator.logic.robot
import javax.script.ScriptEngineManager

class JVMPlatform : Platform {
    override val name: String = "Java ${System.getProperty("java.version")}"
}

actual fun getPlatform(): Platform = JVMPlatform()

actual suspend fun executeRobotCode(code: String, game: RobotApi) {
    withContext(Dispatchers.Default) {
        val engine = ScriptEngineManager().getEngineByExtension("kts")

        println("I'll execute the following code now: ")
        println(code)

        // Make the DSL context available in the script
        engine.put("game", game)
        engine.put("robot", ::robot)
        engine.put("scope", this)

        val fullCode = """
             import net.b0n541.combatsimulator.logic.robot
             import net.b0n541.combatsimulator.logic.RobotApi
             import net.b0n541.combatsimulator.logic.Robot
             import net.b0n541.combatsimulator.logic.Direction.*
             import kotlinx.coroutines.CoroutineScope
             import kotlinx.coroutines.launch
              
             (scope as CoroutineScope).launch {
                 try {
                      ${code.replace("$", "\${'$'}")}
                  } catch (e: Exception) {
                      println("Error executing robot script: ${'$'}{e.message}")
                      e.printStackTrace()
                  }
             }
         """.trimIndent()

        // Run the user code
        try {
            engine.eval(fullCode)
        } catch (e: Exception) {
            println("Error executing robot code: ${e.message}")
            e.printStackTrace()
        }
    }
}