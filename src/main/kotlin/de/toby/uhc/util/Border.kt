package de.toby.uhc.util

import de.toby.uhc.Manager
import de.toby.uhc.config.implementation.Settings
import de.toby.uhc.user.user
import net.axay.kspigot.extensions.onlinePlayers
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object Border {

    private val border = Manager.world.worldBorder

    init {
        border.setCenter(0.0, 0.0)
        border.damageAmount = 2.0
    }

    private fun teleport(player: Player) {
        if (!border.isInside(player.location)) {
            val playerVector = player.location.toVector()
            val spawnVector = Manager.world.spawnLocation.toVector()
            val vector = spawnVector.clone().subtract(playerVector).multiply(1.0 / spawnVector.distance(playerVector)).setY(0.5)
            player.velocity = vector
        }
    }

    fun setSize(size: Double) {
        border.size = size
        onlinePlayers.forEach {
            it.user().board?.update(3, "Border: ${ChatColor.GRAY}${Settings.borderSize}")
            teleport(it)
        }
    }
}