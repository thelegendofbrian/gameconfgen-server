package minepop.confgen

import minepop.dst.DstConfigFile
import java.io.File

fun main(args: Array<String>) {
    val dao = DAO()
    var lastConfigId: Int = -1
    var configFile = DstConfigFile("./", "worldgenoverride.lua")
    configFile.parseConfigFile(File("worldgenoverride.lua")).forEach { config ->
        lastConfigId = dao.insertConfig(config)
        config.configOptions.forEach { configOption ->
            dao.insertConfigOption(lastConfigId, configOption.name, configOption.displayName, configOption.order, configOption.isDefault)
        }
    }
}