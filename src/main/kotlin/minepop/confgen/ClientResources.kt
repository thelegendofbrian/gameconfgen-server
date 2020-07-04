package minepop.confgen

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.jetbrains.exposed.sql.Op

fun main() {
    genClientJson(2)
}

class GameData(var category: String, _contents: MutableList<Contents>) {
    var contents: MutableList<Contents> = _contents.toMutableList()
}
class Contents(var configName: String, var configDesc: String, var configId: String, var displayType: Int?, var defaultOption: Int, _options: MutableList<Option>) {
    var options: MutableList<Option> = _options.toMutableList()
}
data class Option(var id: String = "", var name: String = "", var description: String = "")

fun genClientJson(gameId: Int) {
    val dao = DAO()
    val gson = GsonBuilder().serializeNulls().setPrettyPrinting().create()
    val gameOption = mutableListOf<Option>()
    val gameContents = mutableListOf<Contents>()
    val game = mutableListOf<GameData>()
    dao.retrieveConfigFiles(gameId).forEach { file ->
        dao.retrieveCategories(file.id).forEach { category ->
            dao.retrieveConfigs(category.id).forEach { config ->
                dao.retrieveConfigOptions(config.id).forEach { option ->
                    gameOption += Option(option.name, option.displayName, option.description)
                }
                gameContents += Contents(config.displayName, config.description, config.name, config.displayType, 1, gameOption)
                gameOption.clear()
            }
            game += GameData(category.name, gameContents)
            gameContents.clear()
        }
    }

    println(gson.toJson(game))
}