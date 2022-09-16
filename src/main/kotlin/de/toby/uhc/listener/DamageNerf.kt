package de.toby.uhc.listener

import de.toby.uhc.config.implementation.Settings
import net.axay.kspigot.event.listen
import net.axay.kspigot.extensions.bukkit.isGroundSolid
import net.axay.kspigot.extensions.bukkit.isStandingOnBlock
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.potion.PotionEffectType

object DamageNerf {

    fun enable() {
        listen<EntityDamageByEntityEvent> {
            val damager = it.damager as? Player ?: return@listen
            val item = damager.itemInUse ?: return@listen

            it.damage = when (item.type) {
                Material.WOODEN_AXE -> 3.0
                Material.STONE_AXE -> 4.0
                Material.IRON_AXE -> 5.0
                Material.DIAMOND_AXE -> 6.0
                Material.NETHERITE_AXE -> 7.0
                else -> it.damage
            }

            it.damage *= if (isCriticalHit(damager)) Settings.damageMultiplier else Settings.critMultiplier
        }
    }

    private fun isCriticalHit(player: Player) = player.fallDistance > 0.0f
            && !player.isStandingOnBlock
            && !player.isInsideVehicle
            && player.isGroundSolid
            && !player.hasPotionEffect(PotionEffectType.BLINDNESS)
}