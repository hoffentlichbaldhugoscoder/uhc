package de.toby.uhc.game.implementation

import de.toby.uhc.config.implementation.Settings
import de.toby.uhc.game.Phase
import de.toby.uhc.scoreboard.implementation.endScoreboard
import de.toby.uhc.team.Team
import de.toby.uhc.team.TeamManager
import de.toby.uhc.user.UserState
import de.toby.uhc.user.user
import net.axay.kspigot.chat.literalText
import net.axay.kspigot.extensions.bukkit.title
import net.axay.kspigot.extensions.onlinePlayers
import net.axay.kspigot.extensions.server
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.entity.*
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import java.time.Duration

class End(private val winner: Team?) : Phase() {

    init {
        onlinePlayers.forEach {
            it.user().task?.cancel()
            it.user().board?.layout(endScoreboard)
            updateScoreboard(it)
        }

        event<PlayerJoinEvent> {
            if (it.player.user().state == null) it.player.user().state = UserState.SPECTATING

            it.player.user().board?.layout(endScoreboard)
            updateScoreboard(it.player)
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
    }

    override fun run() {
        if (countdown() == 0) server.spigot().restart()

        onlinePlayers.forEach {
            it.title(
                literalText("${ChatColor.WHITE}Winner:"),
                literalText("${ChatColor.GREEN}${winnerName()}"),
                Duration.ZERO,
                Duration.ofMillis(1050),
                Duration.ZERO
            )
            updateScoreboard(it)
        }
    }

    private fun countdown() = 30 - time

    private fun winnerName(): String {
        if (winner == null) return "Nobody"
        return if (Settings.teams()) "Team #${winner.prefix ?: TeamManager.teams.indexOf(winner)}" else winner.player().first().displayName
    }

    private fun updateScoreboard(player: Player) {
        player.user().board?.run {
            update(3, "${ChatColor.YELLOW}${ChatColor.BOLD}Restart: ${ChatColor.WHITE}${countdown()}")
            update(1, "Winner: ${ChatColor.GREEN}${winnerName()}")
        }
    }
}