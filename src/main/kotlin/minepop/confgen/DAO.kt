package minepop.confgen

import com.natpryce.konfig.ConfigurationProperties
import com.natpryce.konfig.Key
import com.natpryce.konfig.intType
import com.natpryce.konfig.stringType
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
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

    fun insertConfig(configFileId2: Int, name2: String) {
        transaction {
            Config.insert {
                it[configFileId] = configFileId2
                it[name] = name2
                it[displayName] = name2.capitalize()
            }
        }
    }

    object Config: Table("config") {
        private val id = integer("id").autoIncrement()
        val configFileId = integer("config_file_id")
        val name = varchar("name", 45)
        val defaultConfigOptionId = integer("default_config_option_id").nullable()
        val displayName = varchar("display_name", 45)
        val description = varchar("description", 45).nullable()
        val categoryId = integer("category_id").nullable()

        override val primaryKey = PrimaryKey(id)
    }

}

//KEY `config_file_id_idx` (`category_id`),
//CONSTRAINT `category_id` FOREIGN KEY (`category_id`) REFERENCES `config_category` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
//) ENGINE=InnoDB AUTO_INCREMENT=97 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
