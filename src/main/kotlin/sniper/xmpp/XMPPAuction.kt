package sniper.xmpp

import org.jivesoftware.smack.Chat
import org.jivesoftware.smack.XMPPConnection
import sniper.app.Auction
import sniper.app.AuctionEventListener
import sniper.app.AuctionEventListener.PriceSource
import sniper.app.XMPPFailureReporter
import sniper.eventhandling.Announcer

class XMPPAuction(connection: XMPPConnection, itemId: String) : Auction {
    private val auctionEventListeners = Announcer.to(AuctionEventListener::class.java)

    private val chat: Chat

    init {
        val translator = translatorFor(connection)
        val auctionJID = auctionId(itemId, connection)
        chat = connection.chatManager.createChat(auctionJID, translator)
        addAuctionEventListener(chatDisconnectFor(translator))
    }

    private fun chatDisconnectFor(translator: AuctionMessageTranslator): AuctionEventListener {
        return object : AuctionEventListener {
            override fun auctionFailed() {
                chat.removeMessageListener(translator)
            }

            override fun auctionClosed() {}
            override fun currentPrice(price: Int, increment: Int, priceSource: PriceSource) {}
        }
    }

    private fun translatorFor(connection: XMPPConnection) =
            AuctionMessageTranslator(connection.user, auctionEventListeners.announce(), object :XMPPFailureReporter{
                override fun cannotTranslateMessage(auctionId: String, failedMessage: String, exception: Exception) {
                    TODO("Not yet implemented")
                }
            })

    override fun bid(amount: Int) {
        sendMessage(BID_COMMAND_FORMAT.format(amount))
    }

    override fun join() {
        sendMessage(JOIN_COMMAND_FORMAT)
    }

    override fun addAuctionEventListener(listener: AuctionEventListener) {
        auctionEventListeners.addListener(listener)
    }

    private fun sendMessage(message: String) {
        chat.sendMessage(message)
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
