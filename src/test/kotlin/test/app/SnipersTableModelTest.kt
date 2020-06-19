package test.app

import javafx.scene.Scene
import javafx.scene.control.TableView
import javafx.stage.Stage
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.api.FxAssert.verifyThat
import org.testfx.framework.junit5.ApplicationExtension
import org.testfx.framework.junit5.Start
import org.testfx.matcher.control.TableViewMatchers.containsRowAtIndex
import org.testfx.matcher.control.TableViewMatchers.hasNumRows
import sniper.app.SniperListener.SniperSnapshot
import sniper.view.SniperStateData
import sniper.view.SniperStateData.Companion.textFor
import sniper.view.SnipersTableModel
import java.util.function.Predicate

@ExtendWith(ApplicationExtension::class)
class SnipersTableModelTest {

    private lateinit var model: SnipersTableModel

    @Start fun onStart(stage: Stage) {
        model = SnipersTableModel()

        stage.scene = Scene(model.root)
        stage.show()
    }

    @Test fun hasEnoughColumns() {
        verifyThat("#snipers-table", containsColumns("Item", "Last Price", "Last Bid", "State"))
    }

    @Test fun `notifies listener when adding sniper`() {
        val joining = SniperSnapshot.joining("item123")
        verifyThat("#snipers-table", hasNumRows(0))

        model.addSniper(joining)

        verifyThat("#snipers-table", hasNumRows(1))
        verifyThat("#snipers-table", containsRow(joining))
    }

    @Test fun `sets sniper values in columns`() {
        val joining = SniperSnapshot.joining("item id")
        val bidding = joining.bidding(555, 666)

        model.addSniper(joining)
        model.sniperStateChanged(bidding)

        verifyThat("#snipers-table", containsRow(bidding))
    }

    @Test fun `holds snipers in addition order`() {
        val joining0 = SniperSnapshot.joining("item 0")
        val joining1 = SniperSnapshot.joining("item 1")

        model.addSniper(joining0)
        model.addSniper(joining1)

        verifyThat("#snipers-table", containsRow(0, joining0))
        verifyThat("#snipers-table", containsRow(1, joining1))
    }

    @Test fun `updates correct row for snipers`() {
        val joining0 = SniperSnapshot.joining("item 0")
        val joining1 = SniperSnapshot.joining("item 1")
        val bidding0 = joining0.bidding(1, 2)
        val bidding1 = joining1.bidding(3, 3)

        model.addSniper(joining0)
        model.addSniper(joining1)
        model.sniperStateChanged(bidding0)
        model.sniperStateChanged(bidding1)

        verifyThat("#snipers-table", containsRow(0, bidding0))
        verifyThat("#snipers-table", containsRow(1, bidding1))
    }


    private fun containsColumns(vararg columns: String): Predicate<TableView<SniperStateData>> {
        return Predicate { table ->
            assertThat(table.columns.map { c -> c.text }).containsExactly(*columns)
            true
        }
    }

    private fun containsRow(snapshot: SniperSnapshot) = containsRow(0, snapshot)

    private fun containsRow(rowIndex: Int, snapshot: SniperSnapshot) =
            containsRowAtIndex(rowIndex, snapshot.itemId, snapshot.lastPrice, snapshot.lastBid, textFor(snapshot.state))
}
