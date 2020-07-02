package minepop.game

import java.io.File
import java.lang.Exception

class Game(val name: String, val configFiles: MutableList<ConfigFile> = mutableListOf())

abstract class ConfigFile(val path: String, val filename: String, val configs: MutableList<Config> = mutableListOf()) {
    /**
     * Parses a game-specific config file and returns a list of Config's
     */
    abstract fun parseConfigFile(file: File): MutableList<Config>

    /**
     * Uses a list of ConfigFile's to generate a list of game-specific config files
     */
    abstract fun genConfigFile(): String
}

class Config(val name: String, var category: String = "General") {
    var displayName: String = toDisplayName(name)
    var configOptions = mutableListOf<ConfigOption>()

    fun addConfigOption(configOption: ConfigOption) {
        configOptions.add(configOption)
    }

    fun getDefaultOption(): ConfigOption {
        configOptions.forEach {
            if (it.isDefault) {
                return it
            }
        }
        TODO("Make own subclass of exception")
        throw Exception("No default value set for $name config")
    }
}

data class ConfigOption(val name: String, val order: Int, val isDefault: Boolean = false, val displayName: String = name.toLowerCase().capitalize())

fun toDisplayName(name: String) = name.toLowerCase().capitalize().replace("_"," ").replace("-"," ")