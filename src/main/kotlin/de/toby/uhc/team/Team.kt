package de.toby.uhc.team

import de.toby.uhc.user.UserState
import de.toby.uhc.user.user
import net.axay.kspigot.chat.literalText
import net.axay.kspigot.gui.ForInventoryOneByNine
import net.axay.kspigot.gui.GUI
import net.axay.kspigot.gui.GUIType
import net.axay.kspigot.gui.kSpigotGUI
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import java.util.*

class Team {
    var name: String
    var memberColor: ChatColor
    var color: ChatColor
    var prefix: String? = null
    var owner: UUID
    val backpack: GUI<ForInventoryOneByNine> = kSpigotGUI(GUIType.ONE_BY_NINE) {
        title = literalText("Backpack")
    }

    constructor(owner: UUID, name: String, memberColor: ChatColor, color: ChatColor) {
        this.name = name
        this.memberColor = memberColor
        this.color = color
        this.owner = owner
    }

    constructor(owner: UUID, name: String, color: ChatColor) {
        this.name = name
        this.memberColor = color
        this.color = color
        this.owner = owner
    }

    val member = mutableListOf<UUID>()
    fun player() = member.mapNotNull { Bukkit.getPlayer(it) }.filter { it.user().state == UserState.PLAYING }
}