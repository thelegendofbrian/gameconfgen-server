package minepop.confgen

import minepop.dst.DstFormatter
import java.io.File

fun main(args: Array<String>) {
    val dao = DAO()
    var lastConfigId: Int = -1
    var dstFormatter = DstFormatter()
    dstFormatter.parseConfigFile(File("worldgenoverride.lua")).forEach { config ->
        lastConfigId = dao.insertConfig(config)
        config.forEachOption { configOption ->
            dao.insertConfigOption(lastConfigId, configOption.name, configOption.displayName, configOption.order, configOption.isDefault)
        }
    }
}