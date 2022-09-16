package de.toby.uhc.game

object Game {
    var current: Phase? = null
        set(value) {
            field?.stop()
            field = value
        }
}