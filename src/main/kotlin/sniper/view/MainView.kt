package sniper.view

import org.jivesoftware.smack.Chat
import org.jivesoftware.smack.XMPPConnection
import sniper.app.*
import sniper.app.SniperListener.SniperSnapshot
import tornadofx.*

class MainView : View("Auction Sniper") {

    private val data: Data by inject()
    private lateinit var notToBeGCd: Chat

    private val snipers = SnipersTableModel()

    override val root = hbox {
        this += snipers
    }

    init {
        joinAuction(connection(data.hostname, data.username, data.password), data.itemId)
    }

    private fun joinAuction(connection: XMPPConnection, itemId: String) {
        disconnectWhenUICloses(connection)
        val chat = connection.chatManager.createChat(auctionId(itemId, connection), null)
        this.notToBeGCd = chat

        val auction = XMPPAuction(chat)
        chat.addMessageListener(AuctionMessageTranslator(
                connection.user, AuctionSniper(itemId, auction, SniperStateDisplayer(snipers))))
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


    inner class SniperStateDisplayer(private val snipers: SnipersTableModel) : SniperListener {
        override fun sniperStateChanged(snapshot: SniperSnapshot) {
            snipers.sniperStateChanged(snapshot)
        }
    }

    companion object {
        const val AUCTION_RESOURCE: String = "Auction"
        const val JOIN_COMMAND_FORMAT = "SQLVersion: 1.1; Command: JOIN"
        const val BID_COMMAND_FORMAT = "SQLVersion: 1.1; Command: BID; Price: %d"
        private const val ITEM_ID_AS_LOGIN = "auction-%s"
        private const val AUCTION_ID_FORMAT: String = "$ITEM_ID_AS_LOGIN@%s/$AUCTION_RESOURCE"

        const val STATUS_JOINING = "Joining"
        const val STATUS_BIDDING = "Bidding"
        const val STATUS_WINNING = "Winning"
        const val STATUS_LOST = "Lost"
        const val STATUS_WON = "Won"
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
