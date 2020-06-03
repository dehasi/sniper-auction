package test.app

import javafx.collections.FXCollections.observableArrayList
import javafx.scene.Scene
import javafx.scene.control.TableView
import javafx.stage.Stage
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.api.FxAssert.verifyThat
import org.testfx.api.FxRobot
import org.testfx.framework.junit5.ApplicationExtension
import org.testfx.framework.junit5.Start
import org.testfx.matcher.control.TableViewMatchers.containsRow
import org.testfx.util.WaitForAsyncUtils.sleep
import sniper.app.Data
import sniper.app.SniperState
import sniper.view.SniperStateData
import sniper.view.SnipersTableModel
import tornadofx.*
import java.util.concurrent.TimeUnit.SECONDS

@ExtendWith(ApplicationExtension::class)
class MainViewTableTest {

    val sniperState = SniperState("item-xxxxx", 1000, 1002)
    val sniperState2 = SniperState("item-yyyy", 8888, 9999)
    private val row = observableArrayList(SniperStateData(sniperState, "Joining"))
    @Start fun onStart(stage: Stage) {
        val data = Data("", "", "", "")
        setInScope(data, kclass = Data::class)

        val tableView = SnipersTableModel(row)

        stage.scene = Scene(tableView.root)
        stage.show()
    }

    @Test fun table_reacts_on_value_update(robot: FxRobot) {
        verifyThat("#main-table", containsRow(sniperState.itemId, sniperState.lastPrice, sniperState.lastBid, "Joining"))
        row[0] = SniperStateData(sniperState, "3434")
        verifyThat("#main-table", containsRow(sniperState.itemId, sniperState.lastPrice, sniperState.lastBid, "3434"))
    }


    @Test fun table_reacts_on_value_adding(robot: FxRobot) {
        verifyThat("#main-table", containsRow(sniperState.itemId, sniperState.lastPrice, sniperState.lastBid, "Joining"))
        row.add(SniperStateData(sniperState2, "3434"))
        verifyThat("#main-table", containsRow(sniperState.itemId, sniperState.lastPrice, sniperState.lastBid, "3434"))
    }
}
