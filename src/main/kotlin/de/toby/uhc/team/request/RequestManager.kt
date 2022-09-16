package de.toby.uhc.team.request

import de.toby.uhc.team.TeamManager.getTeam
import de.toby.uhc.team.TeamManager.setTeam
import net.axay.kspigot.runnables.taskRunLater
import org.bukkit.entity.Player
import java.util.*

object RequestManager {

    private val requests = mutableMapOf<UUID, MutableList<UUID>>()

    fun Player.sendRequest(target: Player) {
        if (target.hasRequest(this)) return
        if (this == target) return

        val team = getTeam() ?: return
        if (team == target.getTeam()) return

        requests.computeIfAbsent(uniqueId) { mutableListOf() }
        requests[uniqueId]?.add(target.uniqueId)

        taskRunLater((20 * 60 * 5).toLong()) {
            requests[uniqueId]?.remove(target.uniqueId)
        }
    }

    fun Player.acceptRequest(target: Player) {
        if (getTeam() != null) return
        if (this == target) return

        val request = requests[target.uniqueId] ?: return
        if (!request.contains(uniqueId)) return

        val team = target.getTeam() ?: return
        setTeam(team)

        target.removeRequests()
    }

    fun Player.removeRequests() {
        requests.remove(uniqueId)
        requests.values.forEach { it.remove(uniqueId) }
    }

    fun Player.hasRequest(sender: Player) = requests[sender.uniqueId]?.contains(uniqueId) ?: false
}