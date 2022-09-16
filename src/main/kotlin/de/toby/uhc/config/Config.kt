package de.toby.uhc.config

import net.axay.kspigot.main.KSpigotMainInstance
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

abstract class Config(path: String? = null, name: String) {

    private var directory = File("plugins${File.separator}${KSpigotMainInstance.name}${File.separator}${path ?: ""}")
    private var file: File? = null
    private var config: YamlConfiguration

    init {
        directory.mkdirs()

        file = File(directory, "$name.yml")
        file!!.createNewFile()

        config = YamlConfiguration()
        config.load(file!!)
        config.options().copyDefaults(true)
    }

    fun set(path: String, value: Any?) {
        config.set(path, value)
        save()
    }

    fun get(path: String) = config.get(path)

    fun save() {
        file?.let { config.save(it) }
    }
}