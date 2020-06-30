package minepop.dst

import minepop.game.Config
import minepop.game.ConfigOption
import minepop.game.Game
import minepop.game.GameId
import java.io.File

const val pattern = "^\\s*([^ ]*) = \"([^,]*)\",.*-- (\".*\").*"
const val optionsPattern = "\"([^\"]*)\""

fun main(args: Array<String>) {
    var dst: Dst = Dst()
    dst.parseConfig(File("worldgenoverride.lua")).print()
}

class Dst: Game {
    override var configList = Game.ConfigList(GameId.DST)

    override fun parseConfig(file: File): Game.ConfigList {
        var dstConf = file.readLines()
        dstConf.forEach {
            val match = Regex(pattern).find(it)
            if (match != null) {
                val (configName, configValue, configOptions) = match.destructured
                var config: Config = Config(configName)
//                println(configName + " " + configValue)

                val optionMatch = Regex(optionsPattern).findAll(configOptions)
                optionMatch?.forEachIndexed {order, optionString ->
                    val configOptionList = optionString.groupValues.subList(1, optionString.groupValues.size)
                    configOptionList.forEach { option ->
                        config.addConfigOption(ConfigOption(option, order, option == configValue))
                    }
                }
            configList.add(config)
            }
        }

//        configList.add(Config("sample_config"))
//        configList.add(Config("sample_config2"))

        return configList
    }

    override fun genConfig(options: List<String>): Game.ConfigList {
        TODO("Not yet implemented")
    }
}