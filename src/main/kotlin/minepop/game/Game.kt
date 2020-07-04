package minepop.game

import java.io.File

class Game(val name: String, val configFiles: MutableList<ConfigFile> = mutableListOf())

class ConfigFile(val id: Int, val path: String, val filename: String, val configs: MutableList<Config> = mutableListOf()) {

}

class Config(val name: String, val displayName: String = toDisplayName(name)) {
    var id: Int = -1
    var description: String = ""
    var displayType: Int? = null
    constructor(_id: Int, _name: String, _displayName: String = toDisplayName(_name), _displayType: Int?, _description: String?): this(_name, _displayName) {
        id = _id
        displayType = _displayType
        if (_description != null) {
            description = _description
        }
    }

    private var configOptions = mutableListOf<ConfigOption>()

    fun forEachOption(action: (ConfigOption) -> Unit) {
        configOptions.forEach(action)
    }

    fun addConfigOption(configOption: ConfigOption) {
        configOptions.add(configOption)
    }

    fun getDefaultOption(): ConfigOption? {
        configOptions.forEach {
            if (it.isDefault) {
                return it
            }
        }
        return null
    }
}

// TODO: Remove `isDefault`
data class ConfigOption(val name: String, val order: Int, val isDefault: Boolean = false, val displayName: String = toDisplayName(name)) {
    var id: Int = -1
    var description: String = ""
    constructor(_id: Int, _name: String, _order: Int, _displayName: String = toDisplayName(_name), _description: String?) : this(_name, _order) {
        id = _id
        if (_description != null) {
            description = _description
        }
    }
}

data class Category(val id: Int, val name: String)

abstract class ConfigFormatter() {
    /**
     * Parses a game-specific config file and returns a list of `Config`s
     * @param file game-specific config file
     * @return list of `Config`s
     */
    abstract fun parseConfigFile(file: File): MutableList<Config>

    /**
     * Uses a list of `Config`s to generate a game-specific config file
     * @param configFile
     * @return contents of game-specific config file
     */
    abstract fun genConfigFile(configFile: ConfigFile): String
}

fun toDisplayName(name: String) = name.toLowerCase().capitalize().replace("_"," ").replace("-"," ")