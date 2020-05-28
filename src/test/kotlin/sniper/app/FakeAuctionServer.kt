package sniper.app

import org.assertj.core.api.Assertions.assertThat
import org.jivesoftware.smack.Chat
import org.jivesoftware.smack.MessageListener
import org.jivesoftware.smack.XMPPConnection
import org.jivesoftware.smack.packet.Message
import sniper.view.MainView.Companion.AUCTION_RESOURCE
import sniper.view.MainView.Companion.BID_COMMAND_FORMAT
import sniper.view.MainView.Companion.JOIN_COMMAND_FORMAT
import java.lang.String.format
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.TimeUnit.SECONDS

class FakeAuctionServer(val itemId: String) {
    companion object {
        const val XMPP_HOSTNAME = "localhost"
        const val ITEM_ID_AS_LOGIN = "auction-%s"
        const val AUCTION_PASSWORD: String = "auction"
    }

    private var connection: XMPPConnection;
    private lateinit var currentChat: Chat
    private val messageListener: SingleMessageListener = SingleMessageListener()

    init {
        connection = XMPPConnection(XMPP_HOSTNAME)
    }

    fun startSailingItem() {
        connection.connect()
        connection.login(format(ITEM_ID_AS_LOGIN, itemId), AUCTION_PASSWORD, AUCTION_RESOURCE)

        connection.chatManager.addChatListener { chat: Chat, _: Boolean ->
            currentChat = chat
            chat.addMessageListener(messageListener)
        }
    }

    fun hasReceivedJoinRequestFrom(sniperId: String) {
        receivesAMessageMatching(sniperId) { it == JOIN_COMMAND_FORMAT }
    }

    fun announceClosed() {
        currentChat.sendMessage(Message())
    }

    fun stop() {
        connection.disconnect()
    }

    fun reportPrice(price: Int, increment: Int, bidder: String) {
        currentChat.sendMessage("SQLVersion: 1.1; Event: PRICE; " +
                "CurrentPrice: $price; Increment: $increment; Bidder: $bidder")
    }

    fun hasShownSniperIsBidding() {
        TODO("Not yet implemented")
    }

    fun hasReceivedBid(bid: Int, sniperId: String) {
        receivesAMessageMatching(sniperId) { it == BID_COMMAND_FORMAT.format(bid) }
    }

    private fun receivesAMessageMatching(sniperId: String, predicate: (String) -> Boolean) {
        messageListener.receivesAMessage(predicate)
        assertThat(currentChat.participant).isEqualTo(sniperId)
    }
}

class SingleMessageListener : MessageListener {

    private val messages: ArrayBlockingQueue<Message> = ArrayBlockingQueue(1)

    override fun processMessage(chat: Chat?, message: Message?) {
        messages.add(message!!)
    }

    fun receivesAMessage(predicate: (String) -> Boolean) {
        val message = messages.poll(5, SECONDS)
        assertThat(message).isNotNull
        assertThat(message!!.body).matches(predicate)
    }
}
