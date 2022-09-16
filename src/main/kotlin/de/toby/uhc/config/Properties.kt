package de.toby.uhc.config

import kotlin.reflect.KProperty

abstract class Properties(val path: String? = null, val name: String) {

    fun <T : Any?> value(default: T) = object : Delegate<T>(name, path ?: "", default) {}

    abstract class Delegate<T : Any?>(name: String, path: String, val default: T) : Config(path, name) {
        var value: T? = null

        init {
            set(::default.name, default)
        }

        operator fun setValue(thisRef: Any?, property: KProperty<*>, newValue: T) {
            value = newValue
            set(property.name, newValue)
        }

        @Suppress("UNCHECKED_CAST")
        operator fun getValue(thisRef: Any?, property: KProperty<*>) = get(property.name) as? T ?: default
    }
}