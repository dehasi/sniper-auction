package test.app

import javafx.collections.FXCollections.observableArrayList
import javafx.scene.Scene
import javafx.scene.control.TableView
import javafx.stage.Stage
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.api.FxAssert.verifyThat
import org.testfx.api.FxRobot
import org.testfx.framework.junit5.ApplicationExtension
import org.testfx.framework.junit5.Start
import org.testfx.matcher.control.TableViewMatchers.containsRow
import org.testfx.matcher.control.TableViewMatchers.containsRowAtIndex
import sniper.app.Column
import sniper.app.SniperSnapshot
import sniper.app.SniperState.BIDDING
import sniper.app.SniperState.JOINING
import sniper.view.MainView.Companion.STATUS_BIDDING
import sniper.view.MainView.Companion.STATUS_JOINING
import sniper.view.MainView.Companion.STATUS_WINNING
import sniper.view.SniperStateData
import sniper.view.SnipersTableModel

@ExtendWith(ApplicationExtension::class)
class SnipersTableModelTest {

    private val sniperState = SniperSnapshot("item-xxxxx", 1000, 1002, JOINING)
    private val sniperState2 = SniperSnapshot("item-yyyy", 8888, 9999, BIDDING)
    private val row = observableArrayList(SniperStateData(sniperState))

    private lateinit var model: SnipersTableModel

    @Start fun onStart(stage: Stage) {
        model = SnipersTableModel()

        stage.scene = Scene(model.root)
        stage.show()
    }

    @Test fun hasEnoughColumns(robot: FxRobot) {
        val table = robot.lookup("#snipers-table").query<TableView<SniperStateData>>()
        assertThat(table.columns).hasSameSizeAs(Column.values())
    }

    @Test fun setSniperValuesInColumns() {
        model.sniperStatusChanged(sniperState)
        verifyThat("#snipers-table", containsRow(sniperState.itemId, sniperState.lastPrice, sniperState.lastBid, STATUS_JOINING))
        model.sniperStatusChanged(sniperState2)
        verifyThat("#snipers-table", containsRow(sniperState2.itemId, sniperState2.lastPrice, sniperState2.lastBid, STATUS_BIDDING))
    }

    @Test @Disabled("Will be in the future chapters") fun table_reacts_on_value_adding() {
        row.add(SniperStateData(sniperState2))
        verifyThat("#snipers-table", containsRowAtIndex(0, sniperState.itemId, sniperState.lastPrice, sniperState.lastBid, STATUS_JOINING))
        verifyThat("#snipers-table", containsRowAtIndex(1, sniperState2.itemId, sniperState2.lastPrice, sniperState2.lastBid, STATUS_WINNING))
    }
}
