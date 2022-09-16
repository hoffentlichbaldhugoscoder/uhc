package de.toby.uhc.team.request

import de.toby.uhc.team.request.RequestManager.hasRequest
import net.axay.kspigot.chat.literalText
import net.axay.kspigot.event.listen
import net.axay.kspigot.items.flag
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemFlag

object RequestHandler {

    val item = itemStack(Material.GOLDEN_SWORD) {
        meta {
            name = literalText("${ChatColor.AQUA}Invite Player")
            isUnbreakable = true
            flag(ItemFlag.HIDE_UNBREAKABLE)
            flag(ItemFlag.HIDE_ATTRIBUTES)
        }
    }

    fun enable() {
        listen<PlayerInteractAtEntityEvent> {
            if (it.hand == EquipmentSlot.OFF_HAND) return@listen

            val target = it.rightClicked as? Player ?: return@listen
            val item = it.player.inventory.itemInMainHand
            val player = it.player

            if (item.isSimilar(this.item)) {
                it.isCancelled = true

                if (player.hasRequest(target)) player.performCommand("teams accept ${target.displayName}")
                else player.performCommand("teams invite ${target.displayName}")
            }
        }
        listen<EntityDamageByEntityEvent> {
            val player = it.damager as? Player ?: return@listen
            val target = it.entity as? Player ?: return@listen
            val item = player.inventory.itemInMainHand

            if (item.isSimilar(this.item)) {
                it.isCancelled = true

                if (player.hasRequest(target)) player.performCommand("teams accept ${target.displayName}")
                else player.performCommand("teams invite ${target.displayName}")
            }
        }
    }
}