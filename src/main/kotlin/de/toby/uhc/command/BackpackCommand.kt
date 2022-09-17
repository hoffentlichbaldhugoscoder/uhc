package de.toby.uhc.command

import de.toby.uhc.game.Game
import de.toby.uhc.game.implementation.Ingame
import de.toby.uhc.team.TeamManager.getTeam
import net.axay.kspigot.commands.command
import net.axay.kspigot.commands.runs
import net.axay.kspigot.gui.openGUI
import net.axay.kspigot.sound.sound
import org.bukkit.ChatColor
import org.bukkit.Sound

object BackpackCommand {

    fun enable() {
        command("backpack") {
            runs {
                if (Game.current !is Ingame) player.sendMessage("${ChatColor.RED}The game has not started yet")
                else {
                    player.openGUI(player.getTeam()?.backpack ?: return@runs)
                    player.sound(Sound.BLOCK_CHEST_OPEN)
                }
            }
        }
    }
}