package sniper.app

import javafx.stage.Stage
import sniper.view.MainView
import tornadofx.*

class SniperApp : App() {

    override val primaryView = MainView::class

    init {
        importStylesheet(Styles::class)
    }

    override fun start(stage: Stage) {
        config["hostname"] = parameters.named["hostname"]
        config["sniper-id"] = parameters.named["sniper-id"]
        config["sniper-password"] = parameters.named["sniper-password"]

        val data = Data(parameters.named["hostname"]!!,
                parameters.named["sniper-id"]!!,
                parameters.named["sniper-password"]!!)
        setInScope(data)
        super.start(stage)
    }
}
