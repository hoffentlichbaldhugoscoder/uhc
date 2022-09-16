package de.toby.uhc.config

import kotlin.reflect.KProperty

abstract class Properties(val path: String? = null, val name: String) {

    fun <T : Any?> value(default: T) = object : Delegate<T>(name, path ?: "") {
        override var default = default
    }

    abstract class Delegate<T : Any?>(name: String, path: String): Config(path, name) {
        abstract var default: T
        var value: T? = null

        operator fun setValue(thisRef: Any?, property: KProperty<*>, newValue: T) {
            value = newValue
            set(property.name, newValue)
        }

        @Suppress("UNCHECKED_CAST")
        operator fun getValue(thisRef: Any?, property: KProperty<*>) = get(property.name) as? T ?: default
    }
}