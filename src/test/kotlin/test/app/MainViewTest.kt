package test.app

import javafx.scene.Scene
import javafx.scene.control.TextField
import javafx.stage.Stage
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.api.FxRobot
import org.testfx.framework.junit5.ApplicationExtension
import org.testfx.framework.junit5.Start
import sniper.app.Data
import sniper.app.UserRequestListener
import sniper.view.MainView
import test.app.MainViewE2ETest.Companion.HOST_NAME
import test.app.MainViewE2ETest.Companion.SNIPER_ID
import test.app.MainViewE2ETest.Companion.SNIPER_PASSWORD
import tornadofx.*

@ExtendWith(ApplicationExtension::class)
class MainViewTest {

    private lateinit var view: MainView
    @Start fun createView(stage: Stage) {
        val data = Data(HOST_NAME, SNIPER_ID, SNIPER_PASSWORD, listOf(""))
        setInScope(data, kclass = Data::class)

        view = MainView()


        stage.scene = Scene(view.root)
        stage.show()
    }

    @Test fun `makes user request then join button clicked`(robot: FxRobot) {
        val receivedValue = mutableListOf<String>()

        view.addUserRequestListener(object : UserRequestListener {
            override fun joinAuction(itemId: String) {
                receivedValue.add(itemId)
            }
        })

        startBiddingInFor(robot, "an item-id")
        assertThat(receivedValue).containsExactly("an item-id")
    }

    private fun startBiddingInFor(robot: FxRobot, itemId: String) {
        robot.lookup("#item-textbox").query<TextField>().text = itemId
        robot.clickOn("#bid-button")
    }
}
