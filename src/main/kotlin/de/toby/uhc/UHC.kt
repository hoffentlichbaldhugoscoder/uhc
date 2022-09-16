package de.toby.uhc

import de.toby.uhc.command.*
import de.toby.uhc.game.Game
import de.toby.uhc.game.implementation.Lobby
import de.toby.uhc.listener.Connection
import de.toby.uhc.listener.DamageNerf
import de.toby.uhc.team.request.RequestHandler
import de.toby.uhc.ui.SettingsUI
import net.axay.kspigot.main.KSpigot
import org.bukkit.Bukkit
import org.bukkit.World

class UHC : KSpigot() {
    lateinit var world: World

    companion object {
        lateinit var instance: UHC
    }

    override fun load() {
        instance = this
    }

    override fun startup() {
        world = Bukkit.getWorld("world") ?: return

        SettingsCommand.enable()
        StartCommand.enable()
        SpectatorCommand.enable()
        BackpackCommand.enable()
        TeamCommand.enable()
        ReviveCommand.enable()
        FreezeCommand.enable()

        Connection.enable()
        DamageNerf.enable()
        RequestHandler.enable()

        SettingsUI.enable()

        Game.current = Lobby()
    }

    override fun shutdown() {}
}

val Manager by lazy { UHC.instance }