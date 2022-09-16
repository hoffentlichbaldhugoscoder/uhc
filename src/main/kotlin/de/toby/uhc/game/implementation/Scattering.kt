package de.toby.uhc.game.implementation

import de.toby.uhc.Manager
import de.toby.uhc.config.implementation.Settings
import de.toby.uhc.game.Game
import de.toby.uhc.game.Phase
import de.toby.uhc.util.Border
import de.toby.uhc.listener.Connection
import de.toby.uhc.util.PlayerHider.hide
import de.toby.uhc.scoreboard.implementation.mainScoreboard
import de.toby.uhc.team.Team
import de.toby.uhc.team.TeamManager
import de.toby.uhc.user.UserState
import de.toby.uhc.user.user
import de.toby.uhc.util.formatToMinutes
import de.toby.uhc.util.reset
import net.axay.kspigot.extensions.onlinePlayers
import net.axay.kspigot.extensions.server
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*
import kotlin.math.hypot

class Scattering : Phase() {

    private val teams = LinkedList<Team>().also { it.addAll(TeamManager.teams.toList()) }
    private val bar = server.createBossBar(
        NamespacedKey(Manager, "scattering"),
        "Players: ${teams.size}",
        BarColor.RED,
        BarStyle.SOLID
    )

    init {
        onlinePlayers.forEach {
            it.reset()
            it.user().board?.layout(mainScoreboard)
            updateScoreboard(it)
            bar.addPlayer(it)
        }

        onlinePlayers.filter { it.user().state == UserState.SPECTATING }.forEach {
            it.hide()
            it.gameMode = GameMode.SPECTATOR
            it.teleport(Manager.world.spawnLocation)
        }

        Border.setSize(Settings.borderSize.toDouble())

        event<PlayerJoinEvent> { event ->
            val player = event.player
            player.user().board?.layout(mainScoreboard)
            Connection.handleLogin(player)

            onlinePlayers.forEach { updateScoreboard(it) }
        }

        event<PlayerQuitEvent> { event ->
            Connection.handleQuit(event.player)

            onlinePlayers.forEach { updateScoreboard(it) }
        }
    }

    override fun run() {
        teams.forEach { team ->
            val location = getRandomLocation()
            team.player().forEach { it.teleport(location) }
            teams.remove(team)
            bar.setTitle("Players: ${teams.size}")
            bar.progress = teams.size.toDouble()
        }
        if (teams.size == 0) {
            onlinePlayers.forEach { bar.removePlayer(it) }
            server.removeBossBar(NamespacedKey(Manager, "scattering"))
            Game.current = Ingame(1, Settings.graceDuration * 60)
        }
    }

    private fun getRandomLocation(): Location {
        val world = Manager.world
        val size = world.worldBorder.size.toInt() / 2
        var location: Location

        do {
            val x = (-size..size).random()
            val z = (-size..size).random()
            val y = world.getHighestBlockAt(x, z)
            location = Location(world, x.toDouble(), y.location.y, z.toDouble())
        } while (world.getBlockAt(location).isLiquid || world.getBlockAt(location).isPassable)

        return location
    }

    private fun playingPlayers() = onlinePlayers.filter { it.user().state == UserState.PLAYING }.size

    private fun updateScoreboard(player: Player) {
        player.user().board?.run {
            update(4, "Player: ${ChatColor.GRAY}${playingPlayers()}")
        }
    }
}