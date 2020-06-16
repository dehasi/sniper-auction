package sniper.app

import sniper.view.MainView.SwingThreadSniperListener
import sniper.view.SnipersTableModel

class SniperLauncher(private val auctionHouse: AuctionHouse,
                     private val snipers: SnipersTableModel)
    : UserRequestListener {
    private val notToBeGCd = mutableListOf<Auction>()
    private val collector = SniperCollector()


    override fun joinAuction(itemId: String) {
//        snipers.addSniper(SniperListener.SniperSnapshot.joining(itemId))
        val auction = auctionHouse.auctionFor(itemId)
        val sniper = AuctionSniper(itemId, auction, SwingThreadSniperListener(snipers))
        auction.addAuctionEventListener(sniper)
        collector.addSniper(sniper)
        auction.join()
    }
}
