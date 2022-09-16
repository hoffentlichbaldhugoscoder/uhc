package de.toby.uhc.command

import de.toby.uhc.game.Game
import de.toby.uhc.game.implementation.Lobby
import de.toby.uhc.team.TeamManager
import de.toby.uhc.team.TeamManager.leaveTeam
import de.toby.uhc.user.UserState
import de.toby.uhc.user.user
import net.axay.kspigot.commands.command
import net.axay.kspigot.commands.runs
import net.axay.kspigot.extensions.onlinePlayers
import org.bukkit.ChatColor

object SpectatorCommand {

    fun enable() {
        command("spectator") {
            runs {
                if (Game.current !is Lobby) player.sendMessage("${ChatColor.RED}The game already started")
                else {
                    val user = player.user()
                    if (user.state == UserState.SPECTATING) {
                        player.user().state = UserState.PLAYING
                        TeamManager.handleJoin(player)
                        player.sendMessage("${ChatColor.WHITE}You are no longer spectating")
                    } else if (user.state == UserState.PLAYING) {
                        player.user().state = UserState.SPECTATING
                        player.leaveTeam()
                        player.sendMessage("${ChatColor.WHITE}You are now spectating")
                    }
                    onlinePlayers.forEach { player ->
                        player.user().board?.update(1, "Player: ${ChatColor.GRAY}${onlinePlayers.filter { it.user().state == UserState.PLAYING }.size}")
                    }
                }
            }
        }
    }
}