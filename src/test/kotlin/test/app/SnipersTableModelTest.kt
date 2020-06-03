package test.app

import javafx.collections.FXCollections.observableArrayList
import javafx.scene.Scene
import javafx.stage.Stage
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.api.FxAssert.verifyThat
import org.testfx.api.FxRobot
import org.testfx.framework.junit5.ApplicationExtension
import org.testfx.framework.junit5.Start
import org.testfx.matcher.control.TableViewMatchers.containsRow
import org.testfx.matcher.control.TableViewMatchers.containsRowAtIndex
import sniper.app.SniperState
import sniper.view.SniperStateData
import sniper.view.SnipersTableModel

@ExtendWith(ApplicationExtension::class)
class SnipersTableModelTest {

    private val sniperState = SniperState("item-xxxxx", 1000, 1002)
    private val sniperState2 = SniperState("item-yyyy", 8888, 9999)
    private val row = observableArrayList(SniperStateData(sniperState, "Joining"))

    @Start fun onStart(stage: Stage) {
        val tableView = SnipersTableModel(row)

        stage.scene = Scene(tableView.root)
        stage.show()
    }

    @Test fun table_reacts_on_value_update(robot: FxRobot) {
        verifyThat("#main-table", containsRow(sniperState.itemId, sniperState.lastPrice, sniperState.lastBid, "Joining"))
        row[0] = SniperStateData(sniperState, "Bidding")
        verifyThat("#main-table", containsRow(sniperState.itemId, sniperState.lastPrice, sniperState.lastBid, "Bidding"))
    }

    @Test fun table_reacts_on_value_adding(robot: FxRobot) {
        row.add(SniperStateData(sniperState2, "Winning"))
        verifyThat("#main-table", containsRowAtIndex(0, sniperState.itemId, sniperState.lastPrice, sniperState.lastBid, "Joining"))
        verifyThat("#main-table", containsRowAtIndex(1, sniperState2.itemId, sniperState2.lastPrice, sniperState2.lastBid, "Winning"))
    }
}
