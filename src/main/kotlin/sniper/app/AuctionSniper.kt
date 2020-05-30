package sniper.app

class AuctionSniper(private val auction: Auction, private val sniperListener: SniperListener)
    : AuctionEventListener {

    override fun auctionClosed() {
        sniperListener.sniperLost()
    }

    override fun currentPrice(price: Int, increment: Int) {
        auction.bid(price + increment)
        sniperListener.sniperBidding()
    }
}
