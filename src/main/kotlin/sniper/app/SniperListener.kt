package sniper.app

import sniper.app.SniperState.BIDDING
import sniper.app.SniperState.WINNING

interface SniperListener {

    fun sniperLost()
    fun sniperStateChanged(snapshot: SniperSnapshot)
    fun sniperWinning()
    fun sniperWon()

    data class SniperSnapshot(val itemId: String, val lastPrice: Int, val lastBid: Int, val state: SniperState) {
        fun winning(price: Int) = SniperSnapshot(itemId, price, lastBid, WINNING)
        fun bidding(price: Int, bid: Int) = SniperSnapshot(itemId, price, bid, BIDDING)


        companion object {
            fun joining(itemId: String) = SniperSnapshot(itemId, 0, 0, SniperState.JOINING)
        }
    }
}
