package sniper.app

import tornadofx.*

// not forget (FX.application as App).config.string("hostname")
data class Data(val hostname: String, val id: String, val password: String) : Controller()
