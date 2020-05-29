package sniper.app

class AuctionSniper(private val sniperListener: SniperListener) : AuctionEventListener {

    override fun auctionClosed() {
        sniperListener.sniperLost()
    }

    override fun currentPrice(price: Int, increment: Int) {
        TODO("Not yet implemented")
    }
}
