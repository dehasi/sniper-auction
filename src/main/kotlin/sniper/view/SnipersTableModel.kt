package sniper.view

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections.observableArrayList
import sniper.app.SniperListener
import sniper.app.SniperListener.SniperSnapshot
import sniper.app.SniperState
import sniper.view.MainView.Companion.STATUS_BIDDING
import sniper.view.MainView.Companion.STATUS_JOINING
import sniper.view.MainView.Companion.STATUS_LOST
import sniper.view.MainView.Companion.STATUS_WINNING
import sniper.view.MainView.Companion.STATUS_WON
import tornadofx.*

class SnipersTableModel : View(), SniperListener {

    private val snipers = observableArrayList<SniperStateData>()

    override fun sniperStateChanged(newSniperSnapshot: SniperSnapshot) {
        val row = rowMatching(newSniperSnapshot)
        snipers[row] = SniperStateData(newSniperSnapshot)
    }

    private fun rowMatching(snapshot: SniperSnapshot): Int {
        for (i in snipers.indices) {
            if (snipers[i].isForSameItemAs(snapshot)) {
                return i
            }
        }
        TODO("Defect")
    }

    fun addSniper(snapshot: SniperSnapshot) {
        snipers.add(SniperStateData(snapshot))
    }

    override val root = hbox {
        tableview(snipers) {
            id = "snipers-table"
            column("Item", SniperStateData::itemId)
            column("Last Price", SniperStateData::lastPrice)
            column("Last Bid", SniperStateData::lastBid)
            column("State", SniperStateData::status)
        }
    }
}

class SniperStateData(snapshot: SniperSnapshot) {
    fun isForSameItemAs(snapshot: SniperSnapshot): Boolean {
        return itemId.value == snapshot.itemId
    }

    val itemId = SimpleStringProperty(snapshot.itemId)
    val lastPrice = SimpleIntegerProperty(snapshot.lastPrice)
    val lastBid = SimpleIntegerProperty(snapshot.lastBid)
    val status = SimpleStringProperty(textFor(snapshot.state))

    companion object {
        private val STATUS_TEXT = listOf(STATUS_JOINING, STATUS_BIDDING, STATUS_WINNING, STATUS_LOST, STATUS_WON)
        internal fun textFor(state: SniperState) = STATUS_TEXT[state.ordinal]
    }
}
