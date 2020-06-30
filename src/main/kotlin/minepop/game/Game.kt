package minepop.game

import java.io.File
import java.lang.Exception

enum class GameId(val gameName: String) {
    MC("Minecraft"),
    DST("Don't Starve Together"),
    SDTD("Seven Days to Die")
}

interface Game {
    var configList: MutableList<Config>

    /**
        Parses game-specific config and returns config object
     */
    fun parseConfig(file: File): MutableList<Config>

    /**
        Uses a list of config properties to generate a game-specific config
     */
    fun genConfig(options: List<String>): MutableList<Config>

    fun print() {
        configList.forEach {config ->
            println("Name: " + config.name + "/" + config.prettyName)
            config.configOptions.forEach { configOption ->
                println("  ${configOption.name} ${configOption.order} ${configOption.isDefault}")
            }
        }
    }
}

class Config(val name: String, var prettyName: String = name.toLowerCase().capitalize()) {
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