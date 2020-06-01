package sniper.app

import sniper.app.AuctionEventListener.PriceSource

class AuctionSniper(private val auction: Auction, private val sniperListener: SniperListener)
    : AuctionEventListener {

    override fun auctionClosed() {
        sniperListener.sniperLost()
    }

    override fun currentPrice(price: Int, increment: Int, priceSource: PriceSource) {
        auction.bid(price + increment)
        sniperListener.sniperBidding()
    }
}
