package sniper.app

import tornadofx.*

class Data(val hostname: String, val id: String, val password: String) : Controller() {
    constructor() : this(
            (FX.application as App).config.string("hostname"), (
            FX.application as App).config.string("sniper-id"),
            (FX.application as App).config.string("sniper-password"))
}
