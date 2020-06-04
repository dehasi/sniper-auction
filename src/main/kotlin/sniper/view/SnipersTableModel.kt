package sniper.view

import javafx.collections.FXCollections.observableArrayList
import sniper.app.SniperSnapshot
import sniper.app.SniperState.JOINING
import sniper.view.MainView.Companion.STATUS_BIDDING
import sniper.view.MainView.Companion.STATUS_JOINING
import tornadofx.*

class SnipersTableModel : View() {
    companion object {
        private val STARTING_UP = SniperSnapshot("", 0, 0, JOINING)
        private val STATUS_TEXT = listOf(STATUS_JOINING, STATUS_BIDDING)
    }

    private val snipers = observableArrayList(SniperStateData(STARTING_UP, "Joining"))

    fun sniperStatusChanged(newSniperSnapshot: SniperSnapshot) {
        snipers[0] = SniperStateData(newSniperSnapshot, STATUS_TEXT[newSniperSnapshot.state.ordinal])
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
