package de.toby.uhc.ui

import de.toby.uhc.config.implementation.Settings
import de.toby.uhc.game.Game
import de.toby.uhc.team.TeamManager
import de.toby.uhc.team.TeamManager.leaveTeam
import de.toby.uhc.util.hitCooldown
import net.axay.kspigot.chat.literalText
import net.axay.kspigot.event.listen
import net.axay.kspigot.extensions.onlinePlayers
import net.axay.kspigot.extensions.server
import net.axay.kspigot.gui.GUIType
import net.axay.kspigot.gui.Slots
import net.axay.kspigot.gui.kSpigotGUI
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import net.axay.kspigot.items.setLore
import net.axay.kspigot.sound.sound
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemFlag

object SettingsUI {

    val item = itemStack(Material.COMPARATOR) {
        meta { name = literalText("${ChatColor.AQUA}Settings") }
    }

    fun enable() {
        listen<PlayerInteractEvent> {
            if (it.item?.isSimilar(item) == true) it.player.performCommand("settings")
        }
    }

    fun ui() = kSpigotGUI(GUIType.FOUR_BY_NINE) {
        defaultPage = 0
        page(0) {
            title = literalText("Settings")
            placeholder(Slots.All, itemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE) {
                meta { name = literalText() }
            })

            button(Slots.RowThreeSlotThree, autoStart()) {
                Settings.autoStart = !Settings.autoStart
                Game.current?.idle = !(Settings.requiredTeams <= TeamManager.teams.size && Settings.autoStart)
                it.player.sound(Sound.BLOCK_NOTE_BLOCK_XYLOPHONE)
                it.bukkitEvent.currentItem = autoStart()
            }

            button(Slots.RowThreeSlotFour, requiredTeams()) {
                if (it.bukkitEvent.isLeftClick) Settings.requiredTeams += 1
                else if (it.bukkitEvent.isRightClick && Settings.requiredTeams > 0) Settings.requiredTeams -= 1
                Game.current?.idle = !(Settings.requiredTeams <= TeamManager.teams.size && Settings.autoStart)
                it.player.sound(Sound.BLOCK_NOTE_BLOCK_XYLOPHONE)
                it.bukkitEvent.currentItem = requiredTeams()
            }

            nextPage(Slots.RowThreeSlotSix, itemStack(Material.DIAMOND_SWORD) {
                    meta {
                        name = literalText("${ChatColor.AQUA}PVP Settings")
                        setLore {
                            +"${ChatColor.GRAY}Customize the PVP experience"
                        }
                        addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
                    }
                },
                null,
            ) {
                it.player.sound(Sound.BLOCK_NOTE_BLOCK_XYLOPHONE)
            }

            button(Slots.RowTwoSlotThree, timePerDay()) {
                if (it.bukkitEvent.isLeftClick) Settings.timePerDay += 1
                else if (it.bukkitEvent.isRightClick && Settings.timePerDay > 0) Settings.timePerDay -= 1
                it.player.sound(Sound.BLOCK_NOTE_BLOCK_XYLOPHONE)
                it.bukkitEvent.currentItem = timePerDay()
            }

            button(Slots.RowTwoSlotFour, individualTime()) {
                Settings.individualTime = !Settings.individualTime
                it.player.sound(Sound.BLOCK_NOTE_BLOCK_XYLOPHONE)
                it.bukkitEvent.currentItem = individualTime()
            }

            button(Slots.RowThreeSlotFive, graceDuration()) {
                if (it.bukkitEvent.isLeftClick) Settings.graceDuration += 1
                else if (it.bukkitEvent.isRightClick && Settings.graceDuration > 0) Settings.graceDuration -= 1
                it.player.sound(Sound.BLOCK_NOTE_BLOCK_XYLOPHONE)
                it.bukkitEvent.currentItem = graceDuration()
            }

            button(Slots.RowThreeSlotSeven, teamSize()) { event ->
                if (event.bukkitEvent.isLeftClick && Settings.teamSize < server.maxPlayers / 2) Settings.teamSize += 1
                else if (event.bukkitEvent.isRightClick && Settings.teamSize > 0) Settings.teamSize -= 1

                TeamManager.teams.forEach { team ->
                    val member = team.member
                    if (member.size > Settings.teamSize || !Settings.teams()) {
                        team.player().forEach { it.leaveTeam() }
                        TeamManager.teams.remove(team)
                    }
                }

                event.player.sound(Sound.BLOCK_NOTE_BLOCK_XYLOPHONE)
                event.bukkitEvent.currentItem = teamSize()
            }

            button(Slots.RowTwoSlotSix, borderSize()) {
                if (it.bukkitEvent.isLeftClick) Settings.borderSize += 10
                else if (it.bukkitEvent.isRightClick && Settings.borderSize > 0) Settings.borderSize -= 10
                it.player.sound(Sound.BLOCK_NOTE_BLOCK_XYLOPHONE)
                it.bukkitEvent.currentItem = borderSize()
            }

            button(Slots.RowTwoSlotSeven, banAfterDeath()) {
                Settings.banAfterDeath = !Settings.banAfterDeath
                it.player.sound(Sound.BLOCK_NOTE_BLOCK_XYLOPHONE)
                it.bukkitEvent.currentItem = banAfterDeath()
            }
        }
        page(1) {
            title = literalText("Combat Settings")
            placeholder(Slots.All, itemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE) {
                meta { name = literalText() }
            })

            button(Slots.RowThreeSlotThree, hitCooldown()) { event ->
                Settings.hitCooldown = !Settings.hitCooldown
                onlinePlayers.forEach { it.hitCooldown(Settings.hitCooldown) }
                event.player.sound(Sound.BLOCK_NOTE_BLOCK_XYLOPHONE)
                event.bukkitEvent.currentItem = hitCooldown()
            }

            button(Slots.RowThreeSlotFive, damageMultiplier()) {
                if (it.bukkitEvent.isLeftClick) Settings.damageMultiplier += 1
                else if (it.bukkitEvent.isRightClick && Settings.damageMultiplier > 0) Settings.damageMultiplier -= 1
                it.player.sound(Sound.BLOCK_NOTE_BLOCK_XYLOPHONE)
                it.bukkitEvent.currentItem = damageMultiplier()
            }

            button(Slots.RowThreeSlotSeven, critMultiplier()) {
                if (it.bukkitEvent.isLeftClick) Settings.critMultiplier += 1
                else if (it.bukkitEvent.isRightClick && Settings.critMultiplier > 0) Settings.critMultiplier -= 1
                it.player.sound(Sound.BLOCK_NOTE_BLOCK_XYLOPHONE)
                it.bukkitEvent.currentItem = critMultiplier()
            }

            previousPage(Slots.RowOneSlotFive, itemStack(Material.ARROW) {
                meta {
                    name = literalText("${ChatColor.AQUA}Back")
                }
            }, null) {
                it.player.sound(Sound.BLOCK_NOTE_BLOCK_XYLOPHONE)
            }
        }
    }

    private fun autoStart() = itemStack(Material.CLOCK) {
        meta {
            name = literalText("${ChatColor.AQUA}Automatic Countdown")
            setLore {
                +"${ChatColor.GRAY}If enabled the countdown will start"
                +"${ChatColor.GRAY}as soon as enough players are online"
                +""
                +"${ChatColor.GRAY}Current value: ${ChatColor.YELLOW}${Settings.autoStart}"
                +""
                +"${ChatColor.GRAY}Normal Click: ${ChatColor.GOLD}${!Settings.autoStart}"
            }
        }
    }

    private fun requiredTeams() = itemStack(Material.WHITE_BED) {
        meta {
            name = literalText("${ChatColor.AQUA}Required Teams")
            setLore {
                +"${ChatColor.GRAY}The amount of teams required"
                +"${ChatColor.GRAY}for the game to start"
                +""
                +"${ChatColor.GRAY}Current value: ${ChatColor.YELLOW}${Settings.requiredTeams}"
                +""
                +"${ChatColor.GRAY}Click: ${ChatColor.GOLD}+1"
                +"${ChatColor.GRAY}Right Click: ${ChatColor.GOLD}-1"
            }
        }
    }

    private fun graceDuration() = itemStack(Material.GOLDEN_APPLE) {
        meta {
            name = literalText("${ChatColor.AQUA}Grace Duration")
            setLore {
                +"${ChatColor.GRAY}The time the hunters are"
                +"${ChatColor.GRAY}incapable of moving"
                +""
                +"${ChatColor.GRAY}Current time: ${ChatColor.YELLOW}${Settings.graceDuration} minutes"
                +""
                +"${ChatColor.GRAY}Left Click: ${ChatColor.GOLD}+1"
                +"${ChatColor.GRAY}Right Click: ${ChatColor.GOLD}-1"
            }
        }
    }

    private fun timePerDay() = itemStack(Material.COMPASS) {
        meta {
            name = literalText("${ChatColor.AQUA}Time per day")
            setLore {
                +"${ChatColor.GRAY}The time a player can play per day"
                +""
                +"${ChatColor.GRAY}Current time: ${ChatColor.YELLOW}${Settings.timePerDay} minutes"
                +""
                +"${ChatColor.GRAY}Click: ${ChatColor.GOLD}+1"
                +"${ChatColor.GRAY}Right Click: ${ChatColor.GOLD}-1"
            }
        }
    }

    private fun individualTime() = itemStack(Material.COMPASS) {
        meta {
            name = literalText("${ChatColor.AQUA}Individual time")
            setLore {
                +"${ChatColor.GRAY}If a player needs to be online"
                +"${ChatColor.GRAY}as soon as another day begins"
                +""
                +"${ChatColor.GRAY}Current time: ${ChatColor.YELLOW}${Settings.individualTime}"
                +""
                +"${ChatColor.GRAY}Normal Click: ${ChatColor.GOLD}${!Settings.individualTime}"
            }
            addEnchant(Enchantment.DURABILITY, 1, false)
            addItemFlags(ItemFlag.HIDE_ENCHANTS)
        }
    }

    private fun teamSize() = itemStack(Material.RED_BED) {
        meta {
            name = literalText("${ChatColor.AQUA}Team size")
            setLore {
                +"${ChatColor.GRAY}The amount of players"
                +"${ChatColor.GRAY}that can join a team"
                +""
                +"${ChatColor.GRAY}Current size: ${ChatColor.YELLOW}${Settings.teamSize}"
                +""
                +"${ChatColor.GRAY}Click: ${ChatColor.GOLD}+1"
                +"${ChatColor.GRAY}Right Click: ${ChatColor.GOLD}-1"
            }
        }
    }

    private fun borderSize() = itemStack(Material.BARRIER) {
        meta {
            name = literalText("${ChatColor.AQUA}Border size")
            setLore {
                +"${ChatColor.GRAY}The size of the playable area"
                +""
                +"${ChatColor.GRAY}Current size: ${ChatColor.YELLOW}${Settings.borderSize}"
                +""
                +"${ChatColor.GRAY}Click: ${ChatColor.GOLD}+10"
                +"${ChatColor.GRAY}Right Click: ${ChatColor.GOLD}-10"
            }
        }
    }

    private fun banAfterDeath() = itemStack(Material.STONE_PICKAXE) {
        meta {
            name = literalText("${ChatColor.AQUA}Ban after death")
            setLore {
                +"${ChatColor.GRAY}If enabled a player is unable"
                +"${ChatColor.GRAY}to reconnect after dying"
                +""
                +"${ChatColor.GRAY}Current value: ${ChatColor.YELLOW}${Settings.banAfterDeath}"
                +""
                +"${ChatColor.GRAY}Normal Click: ${ChatColor.GOLD}${!Settings.banAfterDeath}"
            }
            addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
        }
    }

    private fun hitCooldown() = itemStack(Material.IRON_SWORD) {
        meta {
            name = literalText("${ChatColor.AQUA}Hitcooldown")
            setLore {
                +"${ChatColor.GRAY}How fast you can swing your sword"
                +""
                +"${ChatColor.GRAY}Current value: ${ChatColor.YELLOW}${Settings.hitCooldown}"
                +""
                +"${ChatColor.GRAY}Normal Click: ${ChatColor.GOLD}${!Settings.hitCooldown}"
            }
            addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
        }
    }

    private fun damageMultiplier() = itemStack(Material.GOLDEN_APPLE) {
        meta {
            name = literalText("${ChatColor.AQUA}Damage Multiplier")
            setLore {
                +""
                +"${ChatColor.GRAY}Current value: ${ChatColor.YELLOW}${(Settings.damageMultiplier.toDouble() / 100)}"
                +""
                +"${ChatColor.GRAY}Left Click: ${ChatColor.GOLD}+0.01"
                +"${ChatColor.GRAY}Right Click: ${ChatColor.GOLD}-0.01"
            }
        }
    }

    private fun critMultiplier() = itemStack(Material.ENCHANTED_GOLDEN_APPLE) {
        meta {
            name = literalText("${ChatColor.AQUA}Crit Damage Multiplier")
            setLore {
                +""
                +"${ChatColor.GRAY}Current value: ${ChatColor.YELLOW}${Settings.critMultiplier.toDouble() / 100}"
                +""
                +"${ChatColor.GRAY}Left Click: ${ChatColor.GOLD}+0.01"
                +"${ChatColor.GRAY}Right Click: ${ChatColor.GOLD}-0.01"
            }
        }
    }
}