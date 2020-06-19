package sniper.app

import sniper.app.AuctionEventListener.PriceSource
import sniper.app.AuctionEventListener.PriceSource.FromOtherBidder
import sniper.app.AuctionEventListener.PriceSource.FromSniper
import sniper.app.SniperListener.SniperSnapshot

class AuctionSniper(private val item: Item,
                    private val auction: Auction)
    : AuctionEventListener {


    private var snapshot = SniperSnapshot.joining(item.identifier)
    private lateinit var sniperListener: SniperListener

    fun getSnapshot() = snapshot

    override fun auctionClosed() {
        snapshot = snapshot.closed()
        notifyChange()
    }

    override fun currentPrice(price: Int, increment: Int, priceSource: PriceSource) {

        snapshot = when (priceSource) {
            FromSniper -> snapshot.winning(price)
            FromOtherBidder -> {
                val bid = price + increment
                if (item.allowsBid(bid)) {
                    auction.bid(bid)
                    snapshot.bidding(price, bid)
                } else {
                    snapshot.losing(price)
                }
            }
        }
        notifyChange()
    }

    override fun auctionFailed() {
        snapshot = snapshot.failed()
        notifyChange()
    }

    private fun notifyChange() {
        sniperListener.sniperStateChanged(snapshot)
    }

    fun addSniperLister(sniperListener: SniperListener) {
        this.sniperListener = sniperListener
    }
}


