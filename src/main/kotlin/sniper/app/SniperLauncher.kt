package sniper.app

import sniper.view.MainView.SwingThreadSniperListener
import sniper.view.SnipersTableModel

class SniperLauncher(private val auctionHouse: AuctionHouse,
                     private val snipers: SnipersTableModel)
    : UserRequestListener {
    private val notToBeGCd = mutableListOf<Auction>()

    override fun joinAuction(itemId: String) {
        snipers.addSniper(SniperListener.SniperSnapshot.joining(itemId))
        val auction = auctionHouse.auctionFor(itemId)

        notToBeGCd.add(auction)

        auction.addAuctionEventListener(AuctionSniper(itemId, auction, SwingThreadSniperListener(snipers)))
        auction.join()
    }
}
