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
        // config["hostname"] = parameters.named["hostname"]
        // (FX.application as App).config.string("hostname")

        val data = Data(parameters.named["hostname"]!!, parameters.named["sniper-id"]!!, parameters.named["sniper-password"]!!)
        setInScope(data)

        super.start(stage)
    }
}
