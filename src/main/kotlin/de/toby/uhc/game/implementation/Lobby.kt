package de.toby.uhc.game.implementation

import de.toby.uhc.config.implementation.Settings
import de.toby.uhc.game.Game
import de.toby.uhc.game.Phase
import de.toby.uhc.scoreboard.implementation.lobbyScoreboard
import de.toby.uhc.team.TeamManager
import de.toby.uhc.team.TeamManager.leaveTeam
import de.toby.uhc.team.request.RequestHandler
import de.toby.uhc.ui.SettingsUI
import de.toby.uhc.user.UserState
import de.toby.uhc.user.user
import de.toby.uhc.util.PlayerHider.show
import de.toby.uhc.util.formatToMinutes
import de.toby.uhc.util.reset
import net.axay.kspigot.extensions.broadcast
import net.axay.kspigot.extensions.bukkit.actionBar
import net.axay.kspigot.extensions.onlinePlayers
import net.axay.kspigot.extensions.pluginManager
import net.axay.kspigot.sound.sound
import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.event.entity.*
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class Lobby : Phase() {

    init {
        val world = Bukkit.getWorld("lobby_map") ?: WorldCreator("lobby_map").generateStructures(false).type(WorldType.FLAT).createWorld()!!
        world.setGameRule(GameRule.RANDOM_TICK_SPEED, 0)
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false)
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false)

        event<PlayerJoinEvent> { event ->
            val player = event.player

            player.reset()
            player.show()
            player.user().board?.layout(lobbyScoreboard)
            player.user().state = UserState.PLAYING
            player.teleport(Location(world, 0.0, world.getHighestBlockYAt(0, 0).toDouble() + 1, 0.0))
            player.inventory.setItem(4, RequestHandler.item)
            player.inventory.setItem(8, SettingsUI.item)

            idle = !(Settings.requiredTeams <= TeamManager.teams.size && Settings.autoStart)

            onlinePlayers.forEach {updateScoreboard(it) }
        }

        event<PlayerQuitEvent> { event ->
            val player = event.player
            player.user().state = null

            player.leaveTeam()

            idle = !(Settings.requiredTeams <= TeamManager.teams.size && Settings.autoStart)
            onlinePlayers.forEach { updateScoreboard(it) }
        }

        event<PlayerDropItemEvent> {
            it.isCancelled = true
        }

        event<PlayerInteractEvent> {
            it.isCancelled = true
        }

        event<EntityPickupItemEvent> {
            it.isCancelled = true
        }

        event<EntitySpawnEvent> {
            it.isCancelled = true
        }

        event<EntityDamageEvent> {
            it.isCancelled = true
        }

        event<FoodLevelChangeEvent> {
            it.isCancelled = true
        }

        event<EntityTargetEvent> {
            it.isCancelled = true
        }

        event<InventoryClickEvent> {
            it.isCancelled = true
        }

        onlinePlayers.forEach {
            pluginManager.callEvent(PlayerJoinEvent(it, null))
        }

        idle = !(Settings.requiredTeams <= TeamManager.teams.size && Settings.autoStart)
    }

    override fun run() {
        onlinePlayers.forEach { updateScoreboard(it) }
        if (!idle) {
            when (countdown()) {
                2, 3, 4, 5, 10, 30 -> broadcast("${ChatColor.YELLOW}The game starts in ${countdown()} seconds")
                1 -> broadcast("${ChatColor.YELLOW}The game starts in one second")
                0 -> {
                    broadcast("${ChatColor.YELLOW}The game has started")
                    Game.current = Scattering()
                }
            }
        } else {
            val text = if (Settings.requiredTeams > TeamManager.teams.size) "Not enough teams are online"
            else if (!Settings.autoStart) "The start is only done via /start" else null

            if (text == null) return
            onlinePlayers.forEach { it.actionBar("${ChatColor.RED}$text") }
        }
    }

    fun countdown() = 30 - time

    private fun playerSize() = onlinePlayers.filter { it.user().state == UserState.PLAYING }.size

    private fun updateScoreboard(player: Player) {
         player.user().board?.run {
             if (!idle) update(3, "${ChatColor.YELLOW}${ChatColor.BOLD}Start: ${ChatColor.WHITE}${countdown().formatToMinutes()}")
             else update(3, "${ChatColor.YELLOW}${ChatColor.BOLD}Start: ${ChatColor.WHITE}Paused")
             update(1, "Player: ${ChatColor.GRAY}${playerSize()}")
         }
    }
}