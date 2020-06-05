package sniper.app

import sniper.app.AuctionEventListener.PriceSource
import sniper.app.AuctionEventListener.PriceSource.FromOtherBidder
import sniper.app.AuctionEventListener.PriceSource.FromSniper
import sniper.app.SniperListener.SniperSnapshot

class AuctionSniper(private val itemId: String,
                    private val auction: Auction,
                    private val sniperListener: SniperListener)
    : AuctionEventListener {

    private var snapshot = SniperSnapshot.joining(itemId)

    override fun auctionClosed() {
        snapshot = snapshot.closed()
        notifyChange()
    }

    override fun currentPrice(price: Int, increment: Int, priceSource: PriceSource) {

        snapshot = when (priceSource) {
            FromSniper -> snapshot.winning(price)
            FromOtherBidder -> {
                val bid = price + increment
                auction.bid(bid)
                snapshot.bidding(price, bid)
            }
        }
        notifyChange()
    }

    private fun notifyChange() {
        sniperListener.sniperStateChanged(snapshot)
    }
}


