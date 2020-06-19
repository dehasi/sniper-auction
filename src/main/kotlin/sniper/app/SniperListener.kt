package sniper.app

import sniper.app.SniperState.*

interface SniperListener {

    fun sniperStateChanged(snapshot: SniperSnapshot)

    data class SniperSnapshot(val itemId: String, val lastPrice: Int, val lastBid: Int, val state: SniperState) {

        fun winning(price: Int) = SniperSnapshot(itemId, price, lastBid, WINNING)
        fun bidding(price: Int, bid: Int) = SniperSnapshot(itemId, price, bid, BIDDING)
        fun closed() = SniperSnapshot(itemId, lastPrice, lastBid, state.whenAuctionClosed())
        fun losing(price: Int) = SniperSnapshot(itemId, price, lastBid, LOSING)
        fun failed() = SniperSnapshot(itemId, 0, 0, FAILED)

        companion object {
            fun joining(itemId: String) = SniperSnapshot(itemId, 0, 0, SniperState.JOINING)
        }
    }
}
