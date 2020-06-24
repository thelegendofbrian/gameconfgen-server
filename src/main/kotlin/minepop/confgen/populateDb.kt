package minepop.confgen

import java.io.File
import com.natpryce.konfig.*
import com.natpryce.konfig.ConfigurationProperties.Companion.systemProperties

const val pattern = "^\\s*([^ ]*) = \"([^,]*)\",.*-- (\".*\").*"
const val optionsPattern = "\"([^\"]*)\""

fun main(args: Array<String>) {
    val dao = DAO()

    File("worldgenoverride.lua").forEachLine {
        val match = Regex(pattern).find(it)
        match?.let {
            val (configName, configValue, configOptions) = match.destructured
//            println("INSERT INTO `confgen`.`config` (`config_file_id`, `name`, `display_name`) VALUES ('1', '$configName', '$configName');")
            println("Inserting $configName")
            dao.insertConfig(1, configName)
//            println("configName = $configName; configValue = $configValue")

            val optionMatch = Regex(optionsPattern).findAll(configOptions)
            optionMatch?.forEach {
                val configOptionList = it.groupValues.subList(1, it.groupValues.size)
                configOptionList.forEach { option ->
//                    println("configOptionList = $option")
                }
            }
        }
    }
}