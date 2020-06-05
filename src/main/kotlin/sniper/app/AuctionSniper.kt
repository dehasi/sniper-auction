package sniper.app

import sniper.app.AuctionEventListener.PriceSource
import sniper.app.SniperListener.SniperSnapshot

class AuctionSniper(private val itemId: String,
                    private val auction: Auction,
                    private val sniperListener: SniperListener)
    : AuctionEventListener {

    private var snapshot = SniperSnapshot.joining(itemId)
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
            snapshot = snapshot.winning(price)
        } else {
            val bid = price + increment
            auction.bid(bid)
            snapshot = snapshot.bidding(price, bid)
        }
        sniperListener.sniperStateChanged(snapshot)
    }
}


