package sniper.app

interface AuctionEventListener {

    fun auctionClosed()
    fun currentPrice(price: Int, increment: Int)
}
