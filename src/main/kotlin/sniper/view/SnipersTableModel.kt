package sniper.view

import javafx.collections.FXCollections
import sniper.app.SniperState
import tornadofx.*

class SnipersTableModel() : View() {

    private val snipers = FXCollections.observableArrayList<SniperStateData>(SniperStateData(SniperState("", 0, 0), "Joining"))

    fun sniperStatusChanged(sniperState: SniperState, status: String) {
        snipers[0] = SniperStateData(sniperState, status)
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
