package sniper.view

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections.observableArrayList
import sniper.app.SniperListener.SniperSnapshot
import sniper.app.SniperState.JOINING
import sniper.view.MainView.Companion.STATUS_BIDDING
import sniper.view.MainView.Companion.STATUS_JOINING
import sniper.view.MainView.Companion.STATUS_WINNING
import tornadofx.*

class SnipersTableModel : View() {
    companion object {
        private val STARTING_UP = SniperSnapshot("", 0, 0, JOINING)
    }

    private val snipers = observableArrayList(SniperStateData(STARTING_UP))

    fun sniperStatusChanged(newSniperSnapshot: SniperSnapshot) {
        snipers[0] = SniperStateData(newSniperSnapshot)
    }

    override val root = hbox {
        tableview(snipers) {
            id = "snipers-table"
            column("itemId", SniperStateData::itemId)
            column("lastPrice", SniperStateData::lastPrice)
            column("lastBid", SniperStateData::lastBid)
            column("Status", SniperStateData::status)
        }
    }
}

class SniperStateData(snapshot: SniperSnapshot) {
    val itemId = SimpleStringProperty(snapshot.itemId)
    val lastPrice = SimpleIntegerProperty(snapshot.lastPrice)
    val lastBid = SimpleIntegerProperty(snapshot.lastBid)
    val status = SimpleStringProperty(STATUS_TEXT[snapshot.state.ordinal])

    companion object {
        private val STATUS_TEXT = listOf(STATUS_JOINING, STATUS_BIDDING, STATUS_WINNING)
    }
}
