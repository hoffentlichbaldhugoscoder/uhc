package de.toby.uhc.command

import de.toby.uhc.game.Game
import de.toby.uhc.game.implementation.Lobby
import de.toby.uhc.ui.SettingsUI
import net.axay.kspigot.commands.command
import net.axay.kspigot.commands.requiresPermission
import net.axay.kspigot.commands.runs
import net.axay.kspigot.gui.openGUI
import org.bukkit.ChatColor

object SettingsCommand {

    fun enable() {
        command("settings") {
            requiresPermission("uhc.settings")
            runs {
                if (Game.current !is Lobby) player.sendMessage("${ChatColor.RED}The game already started")
                else player.openGUI(SettingsUI.ui())
            }
        }
    }
}