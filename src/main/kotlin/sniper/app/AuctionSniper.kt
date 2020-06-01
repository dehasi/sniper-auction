package sniper.app

import sniper.app.AuctionEventListener.PriceSource

class AuctionSniper(private val auction: Auction, private val sniperListener: SniperListener)
    : AuctionEventListener {

    var isWinning = false

    override fun auctionClosed() {
        if (isWinning) {
            sniperListener.sniperWon()
        } else {
            sniperListener.sniperLost()
        }
    }

    override fun currentPrice(price: Int, increment: Int, priceSource: PriceSource) {
        isWinning = priceSource == PriceSource.FromSniper
        if (isWinning) {
            sniperListener.sniperWinning()
        } else {
            auction.bid(price + increment)
            sniperListener.sniperBidding()
        }
    }
}
