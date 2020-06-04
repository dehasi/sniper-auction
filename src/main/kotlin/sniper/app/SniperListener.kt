package sniper.app

interface SniperListener {

    fun sniperLost()
    fun sniperBidding(state: SniperState)
    fun sniperWinning()
    fun sniperWon()
}

data class SniperState(val itemId: String, val lastPrice: Int, val lastBid: Int)
