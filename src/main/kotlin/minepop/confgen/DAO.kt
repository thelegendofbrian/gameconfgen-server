package minepop.confgen

import com.natpryce.konfig.ConfigurationProperties
import com.natpryce.konfig.Key
import com.natpryce.konfig.intType
import com.natpryce.konfig.stringType
import minepop.dst.DstConfigFile
import minepop.game.Config
import minepop.game.ConfigFile
import minepop.game.Game
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File

class DAO {

    init {
        val mySqlDomain = Key("server.domain", stringType)
        val mySqlPort = Key("server.port", intType)
        val mySqlSchema = Key("server.schema", stringType)
        val mySqlUser = Key("server.user", stringType)
        val mySqlPass = Key("server.pass", stringType)

        val config = ConfigurationProperties.fromFile(File("mysql.properties"))

        Database.connect("jdbc:mysql://${config[mySqlDomain]}:${config[mySqlPort]}/${config[mySqlSchema]}",
            driver = "com.mysql.cj.jdbc.Driver", user = config[mySqlUser], password = config[mySqlPass])
    }

    /**
     * Insert config object into database
     */
    fun insertConfig(config: Config): Int {
        var lastConfigId: Int = -1
        transaction {
            lastConfigId = ConfigTable.insert {
                it[configFileId] = 1
                it[name] = config.name
                it[displayName] = config.displayName
            }[ConfigTable.id]
        }
        return lastConfigId
    }

    /**
     * Generate a Game object for a specified game from the database
     */
    fun buildConfigs(gameId2: Int): Game {
        val configs = mutableListOf<Config>()
        transaction {
            (ConfigFileTable innerJoin ConfigTable innerJoin ConfigCategoryTable).slice(ConfigTable.categoryId, ConfigCategoryTable.name).select {
                ConfigFileTable.gameId.eq(gameId2)
            }.groupBy(ConfigTable.categoryId, ConfigCategoryTable.name).forEach { category ->
                var configFile = DstConfigFile(category[ConfigFileTable.path], category[ConfigFileTable.filename])
                configs += Config("name")
            }
        }
        return TODO()
    }

    fun selectConfigs(categoryId: Int) {

    }

    fun insertConfigOption(configId2: Int, name2: String, displayName2: String, order2: Int, isDefault2: Boolean) {
        var lastConfigOptionId: Int = -1

        transaction {
            lastConfigOptionId = ConfigOptionTable.insert {
                it[option] = name2
                it[displayName] = displayName2
                it[order] = order2
                it[configId] = configId2
            }[ConfigOptionTable.id]

            if (isDefault2) {
                ConfigTable.update({ ConfigTable.id eq configId2}) {
                    it[defaultConfigOptionId] = lastConfigOptionId
                }
            }
        }
    }

    object ConfigTable: Table("config") {
        val id = integer("id").autoIncrement()
        val configFileId = integer("config_file_id") references ConfigFileTable.id
        val name = varchar("name", 45)
        val defaultConfigOptionId = integer("default_config_option_id").nullable()
        val displayName = varchar("display_name", 45)
        val description = varchar("description", 45).nullable()
        val categoryId = (integer("category_id") references ConfigCategoryTable.id).nullable()

        override val primaryKey = PrimaryKey(id)
    }

    object ConfigOptionTable: Table("config_option") {
        val id = integer("id").autoIncrement()
        val configId = integer("config_id")
        val order =  integer("order").nullable()
        val option = varchar("option", 45)
        val displayName = varchar("display_name", 45)
        val description = varchar("description", 45).nullable()

        override val primaryKey = PrimaryKey(id)
    }

    object ConfigFileTable: Table("config_file") {
        val id = integer("id").autoIncrement()
        val gameId = integer("game_id")
        val path = varchar("path", 45)
        val filename = varchar("filename", 45)

        override val primaryKey = PrimaryKey(id)
    }

    object ConfigCategoryTable: Table("config_category") {
        val id = integer("id").autoIncrement()
        val name = varchar("name", 45)

        override val primaryKey = PrimaryKey(id)
    }

}