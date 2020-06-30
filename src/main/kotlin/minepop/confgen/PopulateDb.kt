package minepop.confgen

import minepop.dst.Dst
import java.io.File

fun main(args: Array<String>) {
    val dao = DAO()
    var dst: Dst = Dst()
    var lastConfigId: Int = -1
    dst.parseConfig(File("worldgenoverride.lua")).forEach {config ->
        lastConfigId = dao.insertConfig(1, config.name)
        config.configOptions.forEach { configOption ->
            dao.insertConfigOption(lastConfigId, configOption.name, configOption.displayName, configOption.order, configOption.isDefault)
        }
    }
}