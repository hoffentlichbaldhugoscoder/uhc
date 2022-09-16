package de.toby.uhc.util

import org.bukkit.GameMode
import org.bukkit.entity.Player

fun Player.reset() {
    gameMode = GameMode.SURVIVAL
    inventory.clear()
    openInventory.topInventory.clear()
    setItemOnCursor(null)
    updateInventory()
    closeInventory()
    activePotionEffects.clear()
    health = healthScale
    foodLevel = 20
    saturation = 20F
    level = 0
    exp = 0F
}
