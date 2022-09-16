package de.toby.uhc.user

import de.toby.uhc.scoreboard.Board
import net.axay.kspigot.runnables.KSpigotRunnable
import org.bukkit.entity.Player
import java.util.*

class User {
    var state: UserState? = null
    var board: Board? = null
    var task: KSpigotRunnable? = null
    var time = 0
    var kills = 0
    var lastCombatTime = 0L
    var lastOpponent: Player? = null
}

val players = mutableMapOf<UUID, User>()
fun Player.user() = players.computeIfAbsent(uniqueId) { User() }