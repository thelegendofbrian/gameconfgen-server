package minepop.confgen

import com.natpryce.konfig.ConfigurationProperties
import com.natpryce.konfig.Key
import com.natpryce.konfig.intType
import com.natpryce.konfig.stringType
import minepop.game.Category
import minepop.game.Config
import minepop.game.ConfigFile
import minepop.game.ConfigOption
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
     * @param config `Config` object to add to database
     * @return `ConfigTable` `id` value for inserted `Config` object
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
     * Retrieve a list of config files from the database
     * @param gameId
     * @return list of `ConfigFile`s
     */
    fun retrieveConfigFiles(gameId: Int): MutableList<ConfigFile> {
        val configFiles = mutableListOf<ConfigFile>()
        transaction {
            ConfigFileTable.slice(ConfigFileTable.id, ConfigFileTable.path, ConfigFileTable.filename).select {
                ConfigFileTable.gameId.eq(gameId)
            }.forEach { file ->
                configFiles += ConfigFile(file[ConfigFileTable.id], file[ConfigFileTable.path], file[ConfigFileTable.filename])
            }
        }
        return configFiles
    }

    /**
     * Retrieve a list of categories from the database
     * @param configFileId
     * @return list of categories
     */
    fun retrieveCategories(configFileId: Int): MutableList<Category> {
        val cats = mutableListOf<Category>()
        transaction {
            (ConfigTable innerJoin ConfigCategoryTable).slice(ConfigCategoryTable.id, ConfigTable.categoryId, ConfigCategoryTable.name).select {
                ConfigTable.configFileId.eq(configFileId)
            }.groupBy(ConfigTable.categoryId, ConfigCategoryTable.name).forEach { category ->
                cats += Category(category[ConfigCategoryTable.id], category[ConfigCategoryTable.name])
            }
        }
        return cats
    }

    /**
     * Retrieve a list of configs from the database
     * @param categoryId
     * @return list of `Config`s
     */
    fun retrieveConfigs(categoryId: Int): MutableList<Config> {
        val configs = mutableListOf<Config>()
        transaction {
            ConfigTable.slice(ConfigTable.id, ConfigTable.name, ConfigTable.displayName, ConfigTable.description, ConfigTable.displayType).select {
                ConfigTable.categoryId.eq(categoryId)
            }.forEach { config ->
                configs += Config(config[ConfigTable.id], config[ConfigTable.name], config[ConfigTable.displayName], config[ConfigTable.displayType], config[ConfigTable.description])
            }
        }
        return configs
    }

    /**
     * Retrieve a list of config options from the database
     * @param configId
     * @return list of `ConfigOption`s
     */
    fun retrieveConfigOptions(configId: Int): MutableList<ConfigOption> {
        val configOptions = mutableListOf<ConfigOption>()
        transaction {
            ConfigOptionTable.slice(ConfigOptionTable.id, ConfigOptionTable.option, ConfigOptionTable.order, ConfigOptionTable.displayName, ConfigOptionTable.description).select {
                ConfigOptionTable.configId.eq(configId)
            }.orderBy(ConfigOptionTable.order).forEach { option ->
                configOptions += ConfigOption(option[ConfigOptionTable.id], option[ConfigOptionTable.option], option[ConfigOptionTable.order], option[ConfigOptionTable.displayName], option[ConfigOptionTable.description])
            }
        }
        return configOptions
    }

    fun insertConfigOption(configId: Int, name: String, displayName: String, order: Int, isDefault2: Boolean) {
        var lastConfigOptionId: Int = -1

        transaction {
            lastConfigOptionId = ConfigOptionTable.insert {
                it[ConfigOptionTable.option] = name
                it[ConfigOptionTable.displayName] = displayName
                it[ConfigOptionTable.order] = order
                it[ConfigOptionTable.configId] = configId
            }[ConfigOptionTable.id]

            if (isDefault2) {
                ConfigTable.update({ ConfigTable.id eq configId}) {
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
        val displayType = integer("display_type").nullable()
        val categoryId = (integer("category_id") references ConfigCategoryTable.id).nullable()

        override val primaryKey = PrimaryKey(id)
    }

    object ConfigOptionTable: Table("config_option") {
        val id = integer("id").autoIncrement()
        val configId = integer("config_id")
        val order =  integer("order")
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