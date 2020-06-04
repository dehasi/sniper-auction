package sniper.app

interface SniperListener {

    fun sniperLost()
    fun sniperBidding(snapshot: SniperSnapshot)
    fun sniperWinning()
    fun sniperWon()
}

data class SniperSnapshot(val itemId: String, val lastPrice: Int, val lastBid: Int, val state: SniperState)
