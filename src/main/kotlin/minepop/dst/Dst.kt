package minepop.dst

import minepop.game.*
import java.io.File

const val pattern = "^\\s*([^ ]*) = \"([^,]*)\",.*-- (\".*\").*"
const val optionsPattern = "\"([^\"]*)\""

object Games {
    val dst = Game("Don't Starve Together")
}

class DstFormatter(): ConfigFormatter() {
    override fun parseConfigFile(file: File): MutableList<Config> {
        var configs: MutableList<Config> = mutableListOf()
        var dstConf = file.readLines()
        dstConf.forEach {
            val match = Regex(pattern).find(it)
            if (match != null) {
                val (configName, configValue, configOptions) = match.destructured
                var config = Config(configName)

                val optionMatch = Regex(optionsPattern).findAll(configOptions)
                optionMatch?.forEachIndexed {order, optionString ->
                    val configOptions = optionString.groupValues.subList(1, optionString.groupValues.size)
                    configOptions.forEach { option ->
                        config.addConfigOption(ConfigOption(option, order, option == configValue))
                    }
                }
                configs.add(config)
            }
        }
        return configs
    }

    override fun genConfigFile(configFile: ConfigFile): String {
        TODO("Not yet implemented")
    }
}