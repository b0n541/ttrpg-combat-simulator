package net.b0n541.combatsimulator

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform