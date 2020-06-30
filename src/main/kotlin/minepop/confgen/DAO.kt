package minepop.confgen

import com.natpryce.konfig.ConfigurationProperties
import com.natpryce.konfig.Key
import com.natpryce.konfig.intType
import com.natpryce.konfig.stringType
import minepop.confgen.DAO.Config.nullable
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

    fun insertConfig(configFileId2: Int, name2: String): Int {
        var lastConfigId: Int = -1
        transaction {
            lastConfigId = Config.insert {
                it[configFileId] = configFileId2
                it[name] = name2
                it[displayName] = name2.capitalize()
            }[Config.id]
        }
        return lastConfigId
    }

    fun selectConfigs() {
        transaction {
            Config.selectAll()
        }
    }

    fun insertConfigOption(configId2: Int, name2: String, displayName2: String, order2: Int, isDefault2: Boolean) {
        var lastConfigOptionId: Int = -1

        transaction {
            lastConfigOptionId = ConfigOption.insert {
                it[option] = name2
                it[displayName] = displayName2
                it[order] = order2
                it[configId] = configId2
            }[ConfigOption.id]
        }
        transaction {
            if (isDefault2) {
                Config.update({ Config.id eq configId2}) {
                    it[defaultConfigOptionId] = lastConfigOptionId
                }
            }
        }
    }

    object Config: Table("config") {
        val id = integer("id").autoIncrement()
        val configFileId = integer("config_file_id")
        val name = varchar("name", 45)
        val defaultConfigOptionId = integer("default_config_option_id").nullable()
        val displayName = varchar("display_name", 45)
        val description = varchar("description", 45).nullable()
        val categoryId = integer("category_id").nullable()

        override val primaryKey = PrimaryKey(id)
    }

    object ConfigOption: Table("config_option") {
        val id = integer("id").autoIncrement()
        val configId = integer("config_id")
        val order =  integer("order").nullable()
        val option = varchar("option", 45)
        val displayName = varchar("display_name", 45)
        val description = varchar("description", 45).nullable()

        override val primaryKey = PrimaryKey(id)
    }

}