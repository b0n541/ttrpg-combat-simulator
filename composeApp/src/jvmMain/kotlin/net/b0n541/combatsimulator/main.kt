package net.b0n541.combatsimulator

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "TTRPG Combat Simulator",
        state = rememberWindowState(width = 1200.dp, height = 1200.dp)
    ) {
        App()
    }
}