package minepop.confgen

fun main() {
    genClientJson()
}

fun genClientJson() {
    val dao = DAO()

    dao.selectConfigs()
}