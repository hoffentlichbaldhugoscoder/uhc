package de.toby.uhc.command

import de.toby.uhc.Manager
import de.toby.uhc.config.implementation.Settings
import de.toby.uhc.util.PlayerHider.show
import de.toby.uhc.user.UserState
import de.toby.uhc.user.user
import net.axay.kspigot.commands.argument
import net.axay.kspigot.commands.command
import net.axay.kspigot.commands.requiresPermission
import net.axay.kspigot.commands.runs
import net.axay.kspigot.extensions.broadcast
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.GameMode

object ReviveCommand {

    fun enable() {
        command("revive") {
            requiresPermission("uhc.revive")
            argument<String>("player") {
                runs {
                    if (Settings.banAfterDeath) return@runs
                    val target = Bukkit.getPlayer(getArgument<String>("player"))
                    if (target == null) player.sendMessage("${ChatColor.RED}This player is not online")
                    else if (target.user().state == UserState.ELIMINATED) {
                        player.user().state = UserState.PLAYING
                        player.teleport(Manager.world.spawnLocation)
                        player.show()
                        player.gameMode = GameMode.SURVIVAL
                        broadcast("${ChatColor.RED} ${target.displayName} got revived!")
                    } else player.sendMessage("${ChatColor.RED}This player is not eliminated")
                }
            }
        }
    }
}