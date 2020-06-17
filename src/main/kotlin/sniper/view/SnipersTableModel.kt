package sniper.view

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections.observableArrayList
import sniper.app.*
import sniper.app.SniperListener.SniperSnapshot
import tornadofx.*

class SnipersTableModel : View(), SniperListener, SniperCollector, SniperPortfolio.PortfolioListener {

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

    override fun addSniper(sniper: AuctionSniper) {
        addSniper(sniper.getSnapshot())
        sniper.addSniperLister(SwingThreadSniperListener(this))
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

    class SwingThreadSniperListener(private val snipers: SnipersTableModel) : SniperListener {
        override fun sniperStateChanged(snapshot: SniperSnapshot) {
            snipers.sniperStateChanged(snapshot)
        }
    }

    override fun sniperAdded(sniper: AuctionSniper) {
        addSniper(sniper)
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
        const val STATUS_JOINING = "Joining"
        const val STATUS_BIDDING = "Bidding"
        const val STATUS_WINNING = "Winning"
        const val STATUS_LOSING = "Losing"
        const val STATUS_LOST = "Lost"
        const val STATUS_WON = "Won"
        private val STATUS_TEXT = listOf(STATUS_JOINING, STATUS_BIDDING, STATUS_WINNING, STATUS_LOSING, STATUS_LOST, STATUS_WON)

        internal fun textFor(state: SniperState) = STATUS_TEXT[state.ordinal]
    }
}
