package sniper.app

import sniper.view.MainView.SwingThreadSniperListener
import sniper.view.SnipersTableModel

class SniperLauncher(private val auctionHouse: AuctionHouse,
                     private val collector: SniperCollector)
    : UserRequestListener {
    private val notToBeGCd = mutableListOf<Auction>()


    override fun joinAuction(itemId: String) {
//        snipers.addSniper(SniperListener.SniperSnapshot.joining(itemId))
        val auction = auctionHouse.auctionFor(itemId)
        val sniper = AuctionSniper(itemId, auction)
        auction.addAuctionEventListener(sniper)
        collector.addSniper(sniper)
        auction.join()
    }
}
