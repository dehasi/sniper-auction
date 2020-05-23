package sniper.view

import sniper.app.Data
import sniper.app.Styles
import tornadofx.*

class MainView : View("Hello TornadoFX") {
    val data: Data by inject()
    override val root = hbox {
        label(data.hostname) {
            id = "main-label"
            addClass(Styles.heading)
        }
    }
}
