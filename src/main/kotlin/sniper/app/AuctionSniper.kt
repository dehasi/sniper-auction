package sniper.app

import sniper.app.AuctionEventListener.PriceSource
import sniper.app.SniperState.BIDDING

class AuctionSniper(private val itemId: String,
                    private val auction: Auction,
                    private val sniperListener: SniperListener)
    : AuctionEventListener {

    private var isWinning = false

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
            val bid = price + increment
            auction.bid(bid)
            sniperListener.sniperStateChanged(SniperSnapshot(itemId, price, bid, BIDDING))
        }
    }
}
