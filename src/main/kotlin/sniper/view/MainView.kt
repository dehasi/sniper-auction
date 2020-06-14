package sniper.view

import org.jivesoftware.smack.Chat
import org.jivesoftware.smack.XMPPConnection
import sniper.app.*
import sniper.app.SniperListener.SniperSnapshot
import sniper.eventhandling.Announcer
import tornadofx.*

class MainView : View("Auction Sniper") {

    private val data: Data by inject()
    private val notToBeGCd = mutableListOf<XMPPAuction>()

    private val userRequests = Announcer.to(UserRequestListener::class.java)
    private val snipers = SnipersTableModel()

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
        val connection = connection(data.hostname, data.username, data.password)
        disconnectWhenUICloses(connection)
        addUserRequestListenerFor(connection)
    }

    private fun addUserRequestListenerFor(connection: XMPPConnection) {
        addUserRequestListener(object : UserRequestListener {
            override fun joinAuction(itemId: String) {
                snipers.addSniper(SniperSnapshot.joining(itemId))

//                val chat = connection.chatManager.createChat(auctionId(itemId, connection), null)
//                val auctionEventListeners = Announcer.to(AuctionEventListener::class.java)
//                chat.addMessageListener(AuctionMessageTranslator(connection.user, auctionEventListeners.announce()))


                val auction = XMPPAuction(connection, itemId)
                notToBeGCd.add(auction)
                auction.addAuctionEventListener(
                        AuctionSniper(itemId, auction, SwingThreadSniperListener(snipers)))
                auction.join()
            }
        })
    }

    private fun connection(hostname: String, username: String, password: String): XMPPConnection {
        val connection = XMPPConnection(hostname)
        connection.connect()
        connection.login(username, password, AUCTION_RESOURCE)
        return connection
    }



    private fun disconnectWhenUICloses(connection: XMPPConnection) {
        // TODO implement connection.disconnect()
    }


    fun addUserRequestListener(userRequestListener: UserRequestListener) {
        userRequests.addListener(userRequestListener)
    }


    inner class SwingThreadSniperListener(private val snipers: SnipersTableModel) : SniperListener {
        override fun sniperStateChanged(snapshot: SniperSnapshot) {
            snipers.sniperStateChanged(snapshot)
        }
    }

    companion object {
        const val AUCTION_RESOURCE: String = "Auction"

        const val STATUS_JOINING = "Joining"
        const val STATUS_BIDDING = "Bidding"
        const val STATUS_WINNING = "Winning"
        const val STATUS_LOST = "Lost"
        const val STATUS_WON = "Won"
    }
}
