package de.toby.uhc.team


import net.axay.kspigot.extensions.onlinePlayers
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object TeamManager {

    val teams = mutableListOf<Team>()

    fun handleJoin(player: Player) {
        player.getTeam() ?: run {
            player.setTeam(Team(player.uniqueId, player.displayName, ChatColor.GREEN, ChatColor.RED))
        }
    }

    fun Player.setTeam(team: Team) {
        leaveTeam()
        team.member.add(uniqueId)

        onlinePlayers.forEach { it.updateTeams() }
    }

    fun Player.leaveTeam() {
        val team = getTeam() ?: return
        team.member.remove(this.uniqueId)

        onlinePlayers.forEach { it.updateTeams() }
    }

    fun Player.getTeam() = teams.find { it.member.contains(uniqueId) }

    fun Player.isTeamOwner() = getTeam()?.owner == uniqueId

    fun Player.updateTeams() {
        onlinePlayers.forEach {
            val team = it.getTeam()
            if (team != null) {
                val enemy = scoreboard.getTeam("01${teams.indexOf(team)}") ?: scoreboard.registerNewTeam("01${teams.indexOf(team)}")
                enemy.prefix = if(team.prefix != null) "${ChatColor.WHITE}[${team.color}${team.prefix}${ChatColor.WHITE}]" else ""
                enemy.color = team.color
                enemy.addEntry(it.displayName)
            }
            if (getTeam() == null) scoreboard.getTeam("000")?.removeEntry(it.displayName)
            else if (getTeam() == team) {
                val ally = scoreboard.getTeam("000") ?: scoreboard.registerNewTeam("000")
                ally.prefix = if(team!!.prefix != null) "${ChatColor.WHITE}[${team.memberColor}${team.prefix}${ChatColor.WHITE}]" else ""
                ally.color = team.memberColor
                ally.addEntry(it.displayName)
            }
        }
    }
}