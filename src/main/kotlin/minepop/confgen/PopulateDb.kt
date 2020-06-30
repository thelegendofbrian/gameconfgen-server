package minepop.confgen

import minepop.dst.Dst
import java.io.File

fun main(args: Array<String>) {
    val dao = DAO()
    var dst: Dst = Dst()
    dst.parseConfig(File("worldgenoverride.lua")).configList.forEach {config ->
        dao.insertConfig(1, config.name)
        config.configOptions.forEach { configOption ->
            println(configOption.name)
            println(configOption.order)
            println(configOption.isDefault)
            TODO("Create new function in DAO to insert config options into db")
        }
    }
}