package sniper.view

import sniper.app.*
import sniper.app.SniperListener.SniperSnapshot
import sniper.eventhandling.Announcer
import tornadofx.*

class MainView : View("Auction Sniper") {

    private val data: Data by inject()
    private val notToBeGCd = mutableListOf<Auction>()

    private val userRequests = Announcer.to(UserRequestListener::class.java)
    private val snipers = SnipersTableModel()
    private val auctionHouse: AuctionHouse

    override val root = vbox {
        hbox {
            val textfield = textfield {
                id = "item-textbox"
            }
            button("Join Auction") {
                id = "bid-button"
                action {
                    userRequests.announce().joinAuction(textfield.text)
                }
            }
        }
        this += snipers
    }

    init {
        auctionHouse = XMPPAuctionHouse.connect(data.hostname, data.username, data.password)
        disconnectWhenUICloses(auctionHouse)
        addUserRequestListenerFor(auctionHouse)
    }

    private fun addUserRequestListenerFor(auctionHouse: AuctionHouse) {
        addUserRequestListener(object : UserRequestListener {
            override fun joinAuction(itemId: String) {
                val auction = auctionHouse.auctionFor(itemId)

                notToBeGCd.add(auction)

                auction.addAuctionEventListener(AuctionSniper(itemId, auction))
                auction.join()
            }
        })
    }

    private fun disconnectWhenUICloses(auctionHouse: AuctionHouse) {
        // TODO call auctionHouse.disconnect()
    }

    fun addUserRequestListener(userRequestListener: UserRequestListener) {
        userRequests.addListener(userRequestListener)
    }

    companion object {
        const val STATUS_JOINING = "Joining"
        const val STATUS_BIDDING = "Bidding"
        const val STATUS_WINNING = "Winning"
        const val STATUS_LOST = "Lost"
        const val STATUS_WON = "Won"
    }
}
