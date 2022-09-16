package de.toby.uhc.util

import de.toby.uhc.config.implementation.Settings
import de.toby.uhc.util.PlayerHider.hide
import de.toby.uhc.user.UserState
import de.toby.uhc.user.user
import de.toby.uhc.util.skull
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player

fun Player.isInCombat() = user().lastCombatTime + 5000 > System.currentTimeMillis()

fun Player.eliminate() {
    user().state = UserState.ELIMINATED
    gameMode = GameMode.SPECTATOR
    hide()
    if (Settings.banAfterDeath) kickPlayer("${ChatColor.RED}$skull You have been eliminated $skull")
}

fun Player.hitCooldown(enabled: Boolean) {
    if (enabled) hitCooldown(4) else hitCooldown(100)
}

fun Player.hitCooldown(value: Int) {
    getAttribute(Attribute.GENERIC_ATTACK_SPEED)?.baseValue = value.toDouble()
}