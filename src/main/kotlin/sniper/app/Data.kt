package sniper.app

import tornadofx.*

data class Data(val hostname: String,
                val username: String,
                val password: String,
                val items: List<String>) : Controller()
