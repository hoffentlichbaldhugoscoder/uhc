package de.toby.uhc.scoreboard

import de.toby.uhc.user.user
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.scoreboard.DisplaySlot

class Board(player: Player) {

    private val manager = Bukkit.getScoreboardManager()!!
    private val scoreboard = manager.newScoreboard
    private var layout: Layout? = null

    init {
        player.user().board = this
        player.scoreboard = scoreboard
    }

    fun update(line: Int, content: String) {
        val objective = scoreboard.getObjective("display") ?: return
        val score = score(line)
        val entry = ChatColor.values()[score].toString()
        var team = scoreboard.getTeam("$score")
        team ?: run {
            team = scoreboard.registerNewTeam("$score")
            team!!.addEntry(entry)
        }
        team!!.prefix = "${ChatColor.RESET}$content"
        objective.getScore(entry).score = score
    }

    fun remove(score: Int) {
        val entry = ChatColor.values()[score(score)].toString()
        scoreboard.getTeam(entry) ?: return
        scoreboard.resetScores(entry)
    }

    fun removeAt(line: Int) {
        layout?.scores?.asReversed()?.removeAt(line)
        layout(layout ?: return)
    }

    fun addAt(line: Int, content: String) {}

    fun layout(layout: Layout) {
        scoreboard.getObjective("display")?.unregister()
        scoreboard.registerNewObjective("display", "dummy", layout.title)
        scoreboard.getObjective("display")?.displaySlot = DisplaySlot.SIDEBAR

        this.layout = layout

        layout.scores.asReversed().withIndex().forEach {
            update(it.index, it.value ?: "")
        }
    }

    private fun score(score: Int) = Integer.min(score, ChatColor.values().size)
}

open class Layout(val title: String) {
    val scores = mutableListOf<String?>()

    operator fun String?.unaryPlus() {
        scores += this
    }
}

fun scoreboard(title: String, builder: Layout.() -> Unit) = Layout(title).apply(builder)