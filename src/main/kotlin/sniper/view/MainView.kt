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

    private fun auctionId(itemId: String, connection: XMPPConnection): String {
        return String.format(AUCTION_ID_FORMAT, itemId, connection.serviceName)
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
        const val JOIN_COMMAND_FORMAT = "SOLVersion: 1.1; Command: JOIN"
        const val BID_COMMAND_FORMAT = "SOLVersion: 1.1; Command: BID; Price: %d"
        private const val ITEM_ID_AS_LOGIN = "auction-%s"
        private const val AUCTION_ID_FORMAT: String = "$ITEM_ID_AS_LOGIN@%s/$AUCTION_RESOURCE"

        const val STATUS_JOINING = "Joining"
        const val STATUS_BIDDING = "Bidding"
        const val STATUS_WINNING = "Winning"
        const val STATUS_LOST = "Lost"
        const val STATUS_WON = "Won"
    }

    class XMPPAuction(connection: XMPPConnection, itemId: String) : Auction {
        private val auctionEventListeners = Announcer.to(AuctionEventListener::class.java)

        private val chat: Chat

        init {
            chat = connection.chatManager.createChat(auctionId(itemId, connection),
                    AuctionMessageTranslator(connection.user, auctionEventListeners.announce()))
        }

        override fun bid(amount: Int) {
            sendMessage(BID_COMMAND_FORMAT.format(amount))
        }

        override fun join() {
            sendMessage(JOIN_COMMAND_FORMAT)
        }

        private fun sendMessage(message: String) {
            chat.sendMessage(message)
        }

        fun addAuctionEventListener(listener: AuctionEventListener) {
            auctionEventListeners.addListener(listener)
        }

        private fun auctionId(itemId: String, connection: XMPPConnection): String {
            return String.format(AUCTION_ID_FORMAT, itemId, connection.serviceName)
        }
    }
}
