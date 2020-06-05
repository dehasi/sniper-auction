package sniper.view

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections.observableArrayList
import sniper.app.SniperListener
import sniper.app.SniperListener.SniperSnapshot
import sniper.app.SniperState
import sniper.app.SniperState.JOINING
import sniper.view.MainView.Companion.STATUS_BIDDING
import sniper.view.MainView.Companion.STATUS_JOINING
import sniper.view.MainView.Companion.STATUS_LOST
import sniper.view.MainView.Companion.STATUS_WINNING
import sniper.view.MainView.Companion.STATUS_WON
import tornadofx.*

class SnipersTableModel : View(), SniperListener {
    companion object {
        private val STARTING_UP = SniperSnapshot("", 0, 0, JOINING)
    }

    private val snipers = observableArrayList(SniperStateData(STARTING_UP))

    override fun sniperStateChanged(newSniperSnapshot: SniperSnapshot) {
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
//
//    override fun sniperStateChanged(snapshot: SniperSnapshot) {
//        TODO("Not yet implemented")
//    }
}

class SniperStateData(snapshot: SniperSnapshot) {
    val itemId = SimpleStringProperty(snapshot.itemId)
    val lastPrice = SimpleIntegerProperty(snapshot.lastPrice)
    val lastBid = SimpleIntegerProperty(snapshot.lastBid)
    val status = SimpleStringProperty(textFor(snapshot.state))

    private fun textFor(state: SniperState) = STATUS_TEXT[state.ordinal]

    companion object {
        private val STATUS_TEXT = listOf(STATUS_JOINING, STATUS_BIDDING, STATUS_WINNING, STATUS_LOST, STATUS_WON)
    }
}
