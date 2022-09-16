package de.toby.uhc.listener

import de.toby.uhc.config.implementation.Settings
import de.toby.uhc.game.Game
import de.toby.uhc.game.implementation.Lobby
import de.toby.uhc.scoreboard.Board
import de.toby.uhc.team.TeamManager.updateTeams
import de.toby.uhc.team.request.RequestManager.removeRequests
import de.toby.uhc.user.UserState
import de.toby.uhc.user.user
import de.toby.uhc.util.*
import de.toby.uhc.util.PlayerHider.hide
import net.axay.kspigot.event.listen
import net.axay.kspigot.extensions.broadcast
import net.axay.kspigot.extensions.onlinePlayers
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

object Connection {

    fun enable() {
        listen<PlayerJoinEvent>(EventPriority.LOWEST) { event ->
            PlayerHider.handleLogin()

            val player = event.player
            player.hitCooldown(Settings.hitCooldown)
            if (player.user().state == UserState.SPECTATING) {
                player.hide()
                player.gameMode = GameMode.SPECTATOR
            }

            Board(event.player)
            onlinePlayers.forEach {
                it.updateTeams()
            }
        }
        listen<PlayerQuitEvent> {
            it.player.removeRequests()
            it.player.hitCooldown(true)
        }
    }

    fun handleLogin(player: Player) {
        val user = player.user()

        if (user.state == null) user.state = UserState.SPECTATING
        else if (user.state == UserState.ELIMINATED && Settings.banAfterDeath) player.kickPlayer("${ChatColor.RED}$skull You are dead $skull")
    }

    fun handleQuit(player: Player) {
        player.user().task?.cancel()

        if (player.isInCombat()) {
            player.eliminate()
            broadcast("${ChatColor.RED}${player.displayName} left in rage")

            val location = player.location
            player.inventory.contents.forEach {
                if (it != null) location.world?.dropItemNaturally(location, it)
            }
        }
    }
}