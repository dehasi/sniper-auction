package sniper.app

interface Auction {

    fun join()
    fun bid(amount: Int)
    fun addAuctionEventListener(auctionEventListener: AuctionEventListener)
}
