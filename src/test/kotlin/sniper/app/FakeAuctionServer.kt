package sniper.app

import org.assertj.core.api.Assertions.assertThat
import org.jivesoftware.smack.Chat
import org.jivesoftware.smack.MessageListener
import org.jivesoftware.smack.XMPPConnection
import org.jivesoftware.smack.packet.Message
import java.lang.String.format
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.TimeUnit.SECONDS

class FakeAuctionServer(val itemId: String) {
    companion object {
        const val XMPP_HOSTNAME = "localhost"
        const val ITEM_ID_AS_LOGIN = "auction-%s"
        const val AUCTION_PASSWORD: String = "auction"
        const val AUCTION_RESOURCE: String = "Auction"
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

    fun hasReceivedJoinRequestFromSniper() {
        messageListener.receivesAMessage()
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
        assertThat(currentChat.participant).isEqualTo(sniperId)
        messageListener.receivesAMessage { message -> message == "SQLVersion: 1.1; Command: BID; Price: $bid" }
    }
}

class SingleMessageListener : MessageListener {

    private val messages: ArrayBlockingQueue<Message> = ArrayBlockingQueue(1)

    override fun processMessage(chat: Chat?, message: Message?) {
        messages.add(message!!)
    }

    fun receivesAMessage() {
        receivesAMessage { true }
    }

    fun receivesAMessage(predicate: (String) -> Boolean) {
        val message = messages.poll(5, SECONDS)
        assertThat(message).isNotNull
        val body = message!!.body
        assertThat(body).isNotNull
        assertThat(body).matches(predicate)
    }
}
