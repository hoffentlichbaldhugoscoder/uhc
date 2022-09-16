package de.toby.uhc.command

import de.toby.uhc.config.implementation.Settings
import de.toby.uhc.game.Game
import de.toby.uhc.game.implementation.Lobby
import de.toby.uhc.team.TeamManager
import de.toby.uhc.team.TeamManager.getTeam
import de.toby.uhc.team.TeamManager.isTeamOwner
import de.toby.uhc.team.TeamManager.leaveTeam
import de.toby.uhc.team.TeamManager.updateTeams
import de.toby.uhc.team.request.RequestManager.acceptRequest
import de.toby.uhc.team.request.RequestManager.hasRequest
import de.toby.uhc.team.request.RequestManager.sendRequest
import de.toby.uhc.user.UserState
import de.toby.uhc.user.user
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.chat.literalText
import net.axay.kspigot.chat.sendMessage
import net.axay.kspigot.commands.*
import net.axay.kspigot.extensions.onlinePlayers
import net.axay.kspigot.sound.sound
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.ClickEvent.clickEvent
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Sound
import org.bukkit.entity.Player

object TeamCommand {

    fun enable() {
        command("teams") {
            literal("leave") {
                runs {
                    val team = player.getTeam()
                    if (Game.current !is Lobby) player.sendMessage("${ChatColor.RED}The game already started")
                    else if (!Settings.teams()) player.sendMessage("${ChatColor.RED}Teams are disabled")
                    else if (team == null) player.sendMessage("${ChatColor.RED}You have to be in a team")
                    else {
                        player.leaveTeam()
                        TeamManager.handleJoin(player)
                        player.sendMessage("${ChatColor.RED}You left your team")
                        team.player().forEach {
                            it.sendMessage("${ChatColor.YELLOW}${player.displayName}${ChatColor.WHITE} left the team")
                        }
                    }
                }
            }
            literal("invite") {
                argument<String>("player") {
                    suggestListSuspending { onlinePlayers.map(Player::getDisplayName) }
                    runs {
                        val target = Bukkit.getPlayer(getArgument<String>("player")) ?: return@runs

                        if (Game.current !is Lobby) player.sendMessage("${ChatColor.RED}The game already started")
                        else if (!Settings.teams()) player.sendMessage("${ChatColor.RED}Teams are disabled")
                        else if (player.user().state != UserState.PLAYING) player.sendMessage("${ChatColor.RED}You are not participating in the game")
                        else if (player.getTeam() == null) player.sendMessage("${ChatColor.RED}You have to be in a team")
                        else if (!player.isTeamOwner()) player.sendMessage("${ChatColor.RED}You are not the leader")
                        else if (target.hasRequest(player)) player.sendMessage("${ChatColor.RED}You already invited that player")
                        else if (player == target) player.sendMessage("${ChatColor.RED}You cannot invite yourself")
                        else if (player.getTeam() == target.getTeam()) player.sendMessage("${ChatColor.RED}${target.displayName} is already in your team")
                        else {
                            player.sendRequest(target)

                            val component = literalText {
                                text("[Accept]") {
                                    color = KColors.GREEN
                                    clickEvent(ClickEvent.Action.RUN_COMMAND, "/teams accept ${player.displayName}")
                                }
                            }

                            target.sendMessage("${ChatColor.YELLOW}${player.displayName}${ChatColor.WHITE}has invited you to their team")
                            target.sendMessage(component)
                            target.sound(Sound.BLOCK_NOTE_BLOCK_PLING)

                            player.sendMessage("${ChatColor.WHITE}You invited ${ChatColor.YELLOW}${player.displayName}${ChatColor.WHITE} to your team")
                            player.sound(Sound.BLOCK_NOTE_BLOCK_PLING)
                        }
                    }
                }
            }
            literal("accept") {
                argument<String>("player") {
                    suggestListSuspending { onlinePlayers.map(Player::getDisplayName) }
                    runs {
                        val target = Bukkit.getPlayer(getArgument<String>("player")) ?: return@runs

                        if (Game.current !is Lobby) player.sendMessage("${ChatColor.RED}The game already started")
                        else if (!Settings.teams()) player.sendMessage("${ChatColor.RED}Teams are disabled")
                        else if (player.user().state != UserState.PLAYING) player.sendMessage("${ChatColor.RED}You are not participating in the game")
                        else if (!player.hasRequest(target)) player.sendMessage("${ChatColor.RED}This player did not invite you")
                        else if (player == target) player.sendMessage("${ChatColor.RED}How did you invite yourself in first place?")
                        else {
                            if (player.getTeam() != null) player.leaveTeam()
                            player.acceptRequest(target)
                            player.sendMessage("${ChatColor.WHITE}You joined the team of ${ChatColor.YELLOW}${player.displayName}")
                            target.getTeam()?.player()?.forEach {
                                it.sendMessage("${ChatColor.YELLOW}${target.displayName}${ChatColor.WHITE} joined the team")
                            }
                        }
                    }
                }
            }
            literal("kick") {
                argument<String>("player") {
                    suggestListSuspending { onlinePlayers.map(Player::getDisplayName) }
                    runs {
                        val team = player.getTeam()
                        val target = Bukkit.getPlayer(getArgument<String>("player")) ?: return@runs

                        if (Game.current !is Lobby) player.sendMessage("${ChatColor.RED}The game already started")
                        else if (!Settings.teams()) player.sendMessage("${ChatColor.RED}Teams are disabled")
                        else if (team == null) player.sendMessage("${ChatColor.RED}You have to be in a team")
                        else if (!player.isTeamOwner()) player.sendMessage("${ChatColor.RED}You are not the leader")
                        else if (player == target) player.sendMessage("${ChatColor.RED}You cannot kick yourself")
                        else if (target.getTeam() != target.getTeam()) player.sendMessage("${ChatColor.RED}${target.displayName} is not in your team")
                        else {
                            target.leaveTeam()
                            TeamManager.handleJoin(target)
                            target.sendMessage("${ChatColor.RED}You got kicked out of your team")
                            team.player().forEach {
                                it.sendMessage("${ChatColor.YELLOW}${target.displayName}${ChatColor.WHITE}got kicked out of the team")
                            }
                        }
                    }
                }
            }
            literal("owner") {
                argument<String>("player") {
                    suggestListSuspending { onlinePlayers.map(Player::getDisplayName) }
                    runs {
                        val team = player.getTeam()
                        val target = Bukkit.getPlayer(getArgument<String>("player")) ?: return@runs

                        if (Game.current !is Lobby) player.sendMessage("${ChatColor.RED}The game already started")
                        else if (!Settings.teams()) player.sendMessage("${ChatColor.RED}Teams are disabled")
                        else if (team == null) player.sendMessage("${ChatColor.RED}You have to be in a team")
                        else if (!player.isTeamOwner()) player.sendMessage("${ChatColor.RED}You are not the leader")
                        else if (player == target) player.sendMessage("${ChatColor.RED}You are already the leader")
                        else if (team != target.getTeam()) player.sendMessage("${ChatColor.RED}${target.displayName} is not in your team")
                        else {
                            team.owner = target.uniqueId
                            team.player().forEach {
                                it.sendMessage("${ChatColor.YELLOW}${target.displayName}${ChatColor.WHITE} is leading this team now")
                            }
                        }
                    }
                }
            }
            literal("prefix") {
                argument<String>("prefix") {
                    runs {
                        val team = player.getTeam()

                        if (Game.current !is Lobby) player.sendMessage("${ChatColor.RED}The game already started")
                        else if (!Settings.teams()) player.sendMessage("${ChatColor.RED}Teams are disabled")
                        else if (team == null) player.sendMessage("${ChatColor.RED}You have to be in a team")
                        else if (!player.isTeamOwner()) player.sendMessage("${ChatColor.RED}You are not the leader")
                        else {
                            team.prefix = getArgument("prefix")
                            team.player().forEach {
                                it.sendMessage(
                                    "${ChatColor.WHITE}Your team prefix got changed to ${ChatColor.YELLOW}${
                                        getArgument<String>(
                                            "prefix"
                                        )
                                    }"
                                )
                            }
                            onlinePlayers.forEach { it.updateTeams() }
                        }
                    }
                }
            }
        }
    }
}