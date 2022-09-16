package de.toby.uhc.game

import net.axay.kspigot.event.SingleListener
import net.axay.kspigot.event.register
import net.axay.kspigot.event.unregister
import net.axay.kspigot.runnables.KSpigotRunnable
import net.axay.kspigot.runnables.task
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

abstract class Phase {

    private var task: KSpigotRunnable? = null
    val listener = mutableListOf<Listener>()
    var idle = false
        set(value) {
            task?.cancel()
            time = 0
            task = task(period = 20) {
                run()
                if (!value) time++
            }
            field = value
        }

    var time = 0

    init {
        idle = false
    }

    fun stop() {
        task?.cancel()
        listener.forEach(Listener::unregister)
    }

    abstract fun run()

    inline fun <reified T : Event> event(
        priority: EventPriority = EventPriority.NORMAL,
        ignoreCancelled: Boolean = false,
        crossinline onEvent: (event: T) -> Unit,
    ): SingleListener<T> {
        val listener = object : SingleListener<T>(priority, ignoreCancelled) {
            override fun onEvent(event: T) = onEvent.invoke(event)
        }
        listener.register()
        this@Phase.listener += listener
        return listener
    }
}