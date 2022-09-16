package de.toby.uhc.util

import de.toby.uhc.Manager
import net.axay.kspigot.extensions.onlinePlayers
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

object PlayerHider {

    private val hidden = mutableListOf<UUID>()

    fun handleLogin() {
        hidden.mapNotNull(Bukkit::getPlayer).forEach { player -> onlinePlayers.forEach { it.hidePlayer(Manager, player) } }
    }

    fun Player.hide() {
        hidden.add(uniqueId)
        onlinePlayers.forEach {
            it.hidePlayer(Manager, this)
        }
    }

    fun Player.show() {
        hidden.remove(uniqueId)
        onlinePlayers.forEach {
            it.showPlayer(Manager, this)
        }
    }
}