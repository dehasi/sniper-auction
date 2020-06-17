package sniper.app


class SniperLauncher(private val auctionHouse: AuctionHouse,
                     private val collector: SniperCollector)
    : UserRequestListener {

    override fun joinAuction(item: Item) {
        val auction = auctionHouse.auctionFor(item.identifier)
        val sniper = AuctionSniper(item, auction)
        auction.addAuctionEventListener(sniper)
        collector.addSniper(sniper)
        auction.join()
    }
}
