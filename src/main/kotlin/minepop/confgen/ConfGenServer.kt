package minepop.confgen

import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.http.ContentType
import io.ktor.http.Parameters
import io.ktor.http.content.default
import io.ktor.http.content.files
import io.ktor.http.content.static
import io.ktor.request.receiveParameters
import io.ktor.request.receiveText
import io.ktor.response.respondBytes
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.routing.*
import java.io.File

fun main(args: Array<String>) {
    if (System.getProperty("user.timezone") != "UTC") {
        throw Error("Timezone is incorrect!")
    }

//    var configJson = createTempFile("configs","json")
    var configJson = File("sample.json")

    val server = embeddedServer(Netty, port = 8081) {
        routing {
            get("/getconfig") {
                call.respondBytes(configJson.readBytes(), ContentType.Application.Json)
            }
            get("/genconfig") {
                val params = call.parameters

                // Build config
                val pattern = "^\\s*([^ ]*) = \"([^,]*)\",.*-- \".*\".*".toRegex()
                var dstConf = File("worldgenoverride.lua").readLines()
                var retVal = ""
                dstConf.forEach {
                    val match = pattern.find(it)
                    retVal += if (match != null) {
                        val (configName, configValue) = match.destructured
                        it.replaceFirst(configValue, params[configName].toString()) + "\n"
                    } else {
                        it.toString() + "\n"
                    }
                }
                call.respondText(retVal, ContentType.Text.Plain)
            }
        }
    }
    server.start(wait = true)

}