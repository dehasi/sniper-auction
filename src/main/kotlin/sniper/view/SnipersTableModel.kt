package sniper.view

import javafx.collections.ObservableList
import tornadofx.*

class SnipersTableModel(private val row: ObservableList<SniperStateData>) : View() {
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
