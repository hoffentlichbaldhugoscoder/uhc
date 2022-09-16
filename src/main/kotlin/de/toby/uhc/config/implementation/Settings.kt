package de.toby.uhc.config.implementation

import de.toby.uhc.config.Properties

object Settings: Properties(name = "Settings") {
    var timePerDay by value(60)
    var individualTime by value(false)
    var teamSize by value(2)
    var autoStart by value(true)
    var requiredTeams by value(2)
    var borderSize by value(2000)
    var graceDuration by value(30)
    var banAfterDeath by value(true)
    var hitCooldown by value(false)
    var damageMultiplier by value(50)
    var critMultiplier by value(60)

    fun teams() = teamSize > 1
}


