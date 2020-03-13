package sniper.view

import sniper.app.Styles
import tornadofx.*

class MainView : View("Hello TornadoFX") {
    override val root = hbox {
        label(title) {
            id = "main-label"
            addClass(Styles.heading)
        }
    }
}
