package sniper.view

import javafx.beans.property.SimpleStringProperty
import org.jivesoftware.smack.Chat
import org.jivesoftware.smack.XMPPConnection
import sniper.app.*
import tornadofx.*

class MainView : View("Auction Sniper") {

    private val data: Data by inject()
    private val status = SimpleStringProperty()
    private lateinit var notToBeGCd: Chat

    override val root = hbox {
        label(status) {
            id = "main-label"
            addClass(Styles.heading)
        }
    }

    init {
        status.value = "Joining"
        joinAuction(connection(data.hostname, data.username, data.password), data.itemId)
    }

    private fun joinAuction(connection: XMPPConnection, itemId: String) {
        disconnectWhenUICloses(connection)
        val chat = connection.chatManager.createChat(auctionId(itemId, connection), null)
        this.notToBeGCd = chat

        val auction = XMPPAuction(chat)
        chat.addMessageListener(AuctionMessageTranslator(
                connection.user, AuctionSniper(auction, SniperStateDisplayer())))
        auction.join()
    }

    private fun disconnectWhenUICloses(connection: XMPPConnection) {
        // TODO implement connection.disconnect()
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


    inner class SniperStateDisplayer : SniperListener {
        override fun sniperLost() {
            runLater {
                showStatus("Lost")
            }
        }

        override fun sniperBidding() {
            showStatus("Bidding")
        }

        override fun sniperWinning() {
            showStatus("Winning")
        }

        private fun showStatus(value: String) {
            runLater {
                status.value = value
            }
        }
    }

    companion object {
        const val AUCTION_RESOURCE: String = "Auction"
        const val JOIN_COMMAND_FORMAT = "SQLVersion: 1.1; Command: JOIN"
        const val BID_COMMAND_FORMAT = "SQLVersion: 1.1; Command: BID; Price: %d"
        private const val ITEM_ID_AS_LOGIN = "auction-%s"
        private const val AUCTION_ID_FORMAT: String = "$ITEM_ID_AS_LOGIN@%s/$AUCTION_RESOURCE"
    }

    class XMPPAuction(private val chat: Chat) : Auction {
        override fun bid(amount: Int) {
            sendMessage(BID_COMMAND_FORMAT.format(amount))
        }

        override fun join() {
            sendMessage(JOIN_COMMAND_FORMAT)
        }

        private fun sendMessage(message: String) {
            chat.sendMessage(message)
        }
    }
}
