package minepop.dst

import minepop.game.*
import java.io.File

const val pattern = "^\\s*([^ ]*) = \"([^,]*)\",.*-- (\".*\").*"
const val optionsPattern = "\"([^\"]*)\""

object Games {
    val dst = Game("Don't Starve Together")
}

fun main(args: Array<String>) {
    val configOption = ConfigOption("Option 1", 0)
    val config = Config("test_config")
    val configFile = DstConfigFile("ur/path/boi/", "config.txt")
    configFile.configs.add(config)
    Games.dst.configFiles.add(configFile)
}

class DstConfigFile(path: String, filename: String, configs: MutableList<Config> = mutableListOf()): ConfigFile(path, filename, configs) {
    override fun parseConfigFile(file: File): MutableList<Config> {
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

    override fun genConfigFile(): String {
        TODO("Not yet implemented")
    }
}