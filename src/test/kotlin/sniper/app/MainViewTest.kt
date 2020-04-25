package sniper.app

import javafx.scene.Scene
import javafx.stage.Stage
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.api.FxAssert.verifyThat
import org.testfx.framework.junit5.ApplicationExtension
import org.testfx.framework.junit5.Start
import org.testfx.matcher.control.LabeledMatchers.hasText
import sniper.view.MainView

@ExtendWith(ApplicationExtension::class)
class MainViewTest {

    @Start fun onStart(stage: Stage) {
        val view = MainView()
        stage.scene = Scene(view.root)
        stage.show()
    }

    @Test fun should_contain_first_label() {
        verifyThat("#main-label", hasText("Hello TornadoFX"))
    }
}
