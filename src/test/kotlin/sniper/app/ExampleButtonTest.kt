package sniper.app

import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.stage.Stage
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.api.FxAssert.verifyThat
import org.testfx.api.FxRobot
import org.testfx.framework.junit5.ApplicationExtension
import org.testfx.framework.junit5.Start
import org.testfx.matcher.control.LabeledMatchers

@Disabled("It is kept just as an example")
@ExtendWith(ApplicationExtension::class)
class ExampleButtonTest {

    @Start
    fun onStart(stage: Stage) {
        val button = Button("click me")
        button.id = "button1"
        button.setOnAction { button.text = "clicked" }

        stage.scene = Scene(button)
        stage.show();
    }

    @Test
    fun should_contain_first_label(robot: FxRobot) {
        verifyThat("#button1", LabeledMatchers.hasText("click me"))
        robot.clickOn("#button1")
        verifyThat("#button1", LabeledMatchers.hasText("clicked"))
    }
}
