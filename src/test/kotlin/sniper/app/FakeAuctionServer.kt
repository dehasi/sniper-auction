package sniper.app

import org.jivesoftware.smack.Chat
import org.jivesoftware.smack.XMPPConnection
import java.lang.String.format

class FakeAuctionServer(val itemId: String) {
    companion object {
        const val XMPP_HOSTNAME = "localhost"
        const val ITEM_ID_AS_LOGIN = "auction-%s"
        const val AUCTION_PASSWORD: String = "auction"
        const val AUCTION_RESOURCE: String = "Auction"
    }

    private var connection: XMPPConnection;
    private lateinit var currentChat: Chat

    init {
        connection = XMPPConnection(XMPP_HOSTNAME)
    }

    fun startSailingItem() {
        print("Start sailing $itemId")
        connection.connect()
        connection.login(format(ITEM_ID_AS_LOGIN, itemId), AUCTION_PASSWORD, AUCTION_RESOURCE)

        connection.chatManager.addChatListener { chat: Chat, _: Boolean ->
            currentChat = chat
        }
        print("Start sailing works")
    }

    fun hasReceivedJoinRequestFromSniper() {
        // TODO("Not yet implemented")
    }

    fun announceClosed() {
        // TODO("Not yet implemented")
    }
}
