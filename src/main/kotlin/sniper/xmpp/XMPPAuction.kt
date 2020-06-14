package sniper.xmpp

import org.jivesoftware.smack.Chat
import org.jivesoftware.smack.XMPPConnection
import sniper.app.Auction
import sniper.app.AuctionEventListener
import sniper.eventhandling.Announcer

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

    companion object {
        private const val AUCTION_RESOURCE: String = "Auction"
        private const val ITEM_ID_AS_LOGIN = "auction-%s"
        const val JOIN_COMMAND_FORMAT = "SOLVersion: 1.1; Command: JOIN"
        const val BID_COMMAND_FORMAT = "SOLVersion: 1.1; Command: BID; Price: %d"
        private const val AUCTION_ID_FORMAT: String = "$ITEM_ID_AS_LOGIN@%s/$AUCTION_RESOURCE"
    }
}
