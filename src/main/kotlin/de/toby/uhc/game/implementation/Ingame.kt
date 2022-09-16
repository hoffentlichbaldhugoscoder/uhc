package de.toby.uhc.game.implementation

import de.toby.uhc.Manager
import de.toby.uhc.config.implementation.Settings
import de.toby.uhc.game.Game
import de.toby.uhc.game.Phase
import de.toby.uhc.listener.Connection
import de.toby.uhc.scoreboard.implementation.mainScoreboard
import de.toby.uhc.team.TeamManager
import de.toby.uhc.user.UserState
import de.toby.uhc.user.user
import de.toby.uhc.util.eliminate
import de.toby.uhc.util.formatToMinutes
import de.toby.uhc.util.isInCombat
import de.toby.uhc.util.skull
import net.axay.kspigot.extensions.broadcast
import net.axay.kspigot.extensions.onlinePlayers
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityRegainHealthEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class Ingame(private val day: Int, private var graceDuration: Int) : Phase() {

    private var timePerDay = Settings.timePerDay * 60

    init {
        onlinePlayers.forEach {
            Connection.handleLogin(it)
            it.user().time = timePerDay
        }

        Manager.world.run {
            time = 1000
            setStorm(false)
            isThundering = false
        }

        event<PlayerJoinEvent> { event ->
            event.player.user().board?.layout(mainScoreboard)
            Connection.handleLogin(event.player)

            onlinePlayers.forEach {updateScoreboard(it) }
        }

        event<PlayerQuitEvent> { event ->
            Connection.handleQuit(event.player)

            onlinePlayers.forEach { updateScoreboard(it) }
        }

        event<EntityDamageByEntityEvent> {
            val player = it.entity as? Player ?: return@event
            val damager = it.damager as? Player ?: return@event

            if (graceDuration != 0) it.isCancelled = true
            else {
                player.user().lastCombatTime = System.currentTimeMillis()
                player.user().lastOpponent = damager
                damager.user().lastCombatTime = System.currentTimeMillis()
            }
        }

        event<PlayerDeathEvent> {
            val player = it.entity
            player.eliminate()

            if (player.isInCombat()) {
                val opponent = player.user().lastOpponent ?: return@event
                opponent.user().kills++
                updateScoreboard(opponent)

                broadcast("${ChatColor.RED}$skull ${player.displayName} was killed by ${opponent.displayName}")
            } else broadcast("${ChatColor.RED}$skull ${player.displayName} is dead")
            it.deathMessage = null

            if (teamsAlive().size <= 1) Game.current = End(teamsAlive().firstOrNull())
        }

        event<EntityRegainHealthEvent> {
            if (it.entity !is Player) return@event
            if (it.regainReason == EntityRegainHealthEvent.RegainReason.EATING) return@event
            it.isCancelled = true
        }
    }

    override fun run() {
        onlinePlayers.forEach {
            updateScoreboard(it)
            if (Settings.individualTime) {
                val user = it.user()
                if (user.state == UserState.PLAYING) {
                    if (user.time == 0) {
                        if (it.isInCombat()) {
                            user.time = 600
                            it.sendMessage("${ChatColor.RED}You got more time since you are in combat")
                        } else it.kickPlayer("${ChatColor.RED}You ran out of time")
                    }
                    user.time--
                }
            } else {
                timePerDay -= 1
                if (timePerDay == 0) it.kickPlayer("${ChatColor.RED}You ran out of time")
            }
        }

        if (graceDuration > 0) {
            graceDuration--
            when (graceDuration) {
                120, 360, 240, 300, 600, 1800 -> broadcast("${ChatColor.YELLOW}The grace period ends in ${graceDuration / 60} minutes")
                2, 3, 4, 5, 10, 30, 60 -> broadcast("${ChatColor.YELLOW}The grace period ends in $graceDuration seconds")
                1 -> broadcast("${ChatColor.YELLOW}The grace period ends in one second")
                0 -> {
                    broadcast("${ChatColor.YELLOW}The grace period has ended")
                    onlinePlayers.forEach {
                        it.health = it.healthScale
                        it.foodLevel = 20
                    }
                    broadcast("${ChatColor.GREEN}You got healed for the last time")
                }
            }
        }

        when (countdown()) {
            120, 360, 240, 300, 600, 1800 -> broadcast("${ChatColor.RED}The day ends in ${countdown()} minutes")
            2, 3, 4, 5, 10, 30, 60 -> broadcast("${ChatColor.RED}The day ends in ${countdown()} seconds")
            1 -> broadcast("${ChatColor.YELLOW}The day ends in one second")
            0 -> {
                broadcast("${ChatColor.RED}The day has ended")
                onlinePlayers.forEach { it.kickPlayer("${ChatColor.RED}The day has ended") }
                Game.current = Ingame(day + 1, 0)
            }
        }
    }

    private fun countdown() = 86400 - time

    private fun teamsAlive() = TeamManager.teams.filter { team ->
        team.player().any { it.user().state == UserState.PLAYING }
    }

    private fun playerAlive() = onlinePlayers.filter { it.user().state == UserState.PLAYING }.size

    private fun updateScoreboard(player: Player) {
        player.user().board?.run {
            if (graceDuration > 0) update(6, "${ChatColor.YELLOW}${ChatColor.BOLD}Grace: ${ChatColor.WHITE}${graceDuration.formatToMinutes()}")
            else update(6, "${ChatColor.YELLOW}${ChatColor.BOLD}Day $day")
            update(4, "Player: ${ChatColor.GRAY}${playerAlive()}")
            update(2, "${ChatColor.WHITE}Kills: ${ChatColor.GRAY}${player.user().kills}")
        }
    }
}