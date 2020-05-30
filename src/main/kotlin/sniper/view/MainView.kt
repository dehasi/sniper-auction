package sniper.view

import javafx.beans.property.SimpleStringProperty
import org.jivesoftware.smack.Chat
import org.jivesoftware.smack.MessageListener
import org.jivesoftware.smack.XMPPConnection
import sniper.app.*
import tornadofx.*

class MainView : View("Auction Sniper"), SniperListener {

    private val data: Data by inject()
    private val status = SimpleStringProperty()
    private lateinit var notToBeGCd: Chat

    override val root = hbox {
        label {
            id = "main-label"
            addClass(Styles.heading)
            bind(status)
        }
    }

    init {
        status.value = "Joining"
        joinAuction(connection(data.hostname, data.username, data.password), data.itemId)
    }

    private fun joinAuction(connection: XMPPConnection, itemId: String) {
        disconnectWhenUICloses(connection)
        val chat = connection.chatManager.createChat(auctionId(itemId, connection),
//                MessageListener { chat, message ->
//                    println("message:" + message?.body)
//                }
                null)
        this.notToBeGCd = chat

        val auction =object: Auction {
             override fun bid(amount: Int) {
                chat.sendMessage(BID_COMMAND_FORMAT.format(amount))
            }
        }
        chat.addMessageListener(AuctionMessageTranslator(AuctionSniper(auction, this)))
        chat.sendMessage(JOIN_COMMAND_FORMAT)
    }

    private fun disconnectWhenUICloses(connection: XMPPConnection) {
//        if (onUndockListeners == null) onUndockListeners = mutableListOf()
//        onUndockListeners!!.add {
//            currentWindow?.setOnCloseRequest {
//                println("Closing")
//                connection.disconnect()
//            }
//        }
    }

    override fun sniperLost() {
        status.value = "Lost"
    }

    override fun sniperBidding() {
        status.value = "Bidding"
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

    companion object {
        const val AUCTION_RESOURCE: String = "Auction"
        const val JOIN_COMMAND_FORMAT = "SQLVersion: 1.1; Command: JOIN"
        const val BID_COMMAND_FORMAT = "SQLVersion: 1.1; Command: BID; Price: %d"
        private const val ITEM_ID_AS_LOGIN = "auction-%s"
        private const val AUCTION_ID_FORMAT: String = "$ITEM_ID_AS_LOGIN@%s/$AUCTION_RESOURCE"
    }

//    override fun onDock() {
//        currentWindow?.setOnCloseRequest {
//            println("Closing!!!")
//        }
//    }
}
