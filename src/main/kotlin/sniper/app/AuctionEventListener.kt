package sniper.app

interface AuctionEventListener {

    enum class PriceSource {
        FromSniper, FromOtherBidder
    }

    fun auctionClosed()
    fun currentPrice(price: Int, increment: Int, priceSource: PriceSource)
}
