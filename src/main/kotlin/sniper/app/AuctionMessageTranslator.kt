package sniper.app

import org.jivesoftware.smack.Chat
import org.jivesoftware.smack.MessageListener
import org.jivesoftware.smack.packet.Message

open class AuctionMessageTranslator(private val listener: AuctionEventListener) : MessageListener {
    override fun processMessage(chat: Chat?, message: Message?) {
        listener.auctionClosed()
    }
}
