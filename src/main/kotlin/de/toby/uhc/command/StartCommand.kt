package de.toby.uhc.command

import de.toby.uhc.game.Game
import de.toby.uhc.game.implementation.Lobby
import de.toby.uhc.user.user
import de.toby.uhc.util.formatToMinutes
import net.axay.kspigot.commands.command
import net.axay.kspigot.commands.requiresPermission
import net.axay.kspigot.commands.runs
import net.axay.kspigot.extensions.onlinePlayers
import org.bukkit.ChatColor

object StartCommand {

    fun enable() {
        command("start") {
            requiresPermission("uhc.forceStart")
            runs {
                val current = Game.current

                if (current !is Lobby) player.sendMessage("${ChatColor.RED}The game already started")
                else if(!current.idle) player.sendMessage("${ChatColor.RED}The countdown already started")
                else {
                    current.idle = false
                    onlinePlayers.forEach {
                        it.user().board?.update(
                            3,
                            "${ChatColor.YELLOW}${ChatColor.BOLD}Start: ${ChatColor.WHITE}${
                                current.countdown().formatToMinutes()
                            }"
                        )
                    }
                }
            }
        }
    }
}