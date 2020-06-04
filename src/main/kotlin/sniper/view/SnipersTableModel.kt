package sniper.view

import javafx.collections.ObservableList
import sniper.app.SniperState
import tornadofx.*

class SnipersTableModel(private val row: ObservableList<SniperStateData>) : View() {

    fun sniperStatusChanged(sniperState: SniperState, status: String) {
        row[0] = SniperStateData(sniperState, status)
    }

    override val root = hbox {
        tableview(row) {
            id = "snipers-table"
            column("itemId", SniperStateData::itemId)
            column("lastPrice", SniperStateData::lastPrice)
            column("lastBid", SniperStateData::lastBid)
            column("Status", SniperStateData::status)
        }
    }


}
