package sniper.view

import javafx.collections.FXCollections.observableArrayList
import sniper.app.SniperState
import tornadofx.*

class SnipersTableModel() : View() {
    companion object {
        private val STARTING_UP = SniperState("", 0, 0)
    }

    private val snipers = observableArrayList(SniperStateData(STARTING_UP, "Joining"))

    fun sniperStatusChanged(newSniperState: SniperState, newtStatusTest: String) {
        snipers[0] = SniperStateData(newSniperState, newtStatusTest)
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
