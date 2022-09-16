package de.toby.uhc.scoreboard.implementation

import de.toby.uhc.scoreboard.scoreboard
import org.bukkit.ChatColor

val lobbyScoreboard = scoreboard("${ChatColor.AQUA}${ChatColor.BOLD}     UHC     ") {
    +""
    +"${ChatColor.YELLOW}${ChatColor.BOLD}Start: ${ChatColor.WHITE}00:00"
    +""
    +"Player: ${ChatColor.GRAY}0"
    +""
}

val mainScoreboard = scoreboard("${ChatColor.AQUA}${ChatColor.BOLD}     UHC     ") {
    +""
    +"${ChatColor.YELLOW}${ChatColor.BOLD}Day 1"
    +""
    +"${ChatColor.WHITE}Player: ${ChatColor.GRAY}0"
    +"${ChatColor.WHITE}Border: ${ChatColor.GRAY}0"
    +""
    +"${ChatColor.WHITE}Kills: ${ChatColor.GRAY}0"
    +""
}

val endScoreboard = scoreboard("${ChatColor.AQUA}${ChatColor.BOLD}     UHC     ") {
    +""
    +"${ChatColor.YELLOW}${ChatColor.BOLD}Restart: ${ChatColor.WHITE}00"
    +""
    +"Winner: ${ChatColor.GREEN}Nobody"
    +""
}