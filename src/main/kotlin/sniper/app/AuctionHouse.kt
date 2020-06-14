package sniper.app

interface AuctionHouse {

    fun auctionFor(itemId: String): Auction
}
