package sniper.app

import org.jivesoftware.smack.Chat
import org.jivesoftware.smack.MessageListener
import org.jivesoftware.smack.packet.Message

open class AuctionMessageTranslator(private val listener: AuctionEventListener) : MessageListener {

    override fun processMessage(chat: Chat?, message: Message?) {
        val event: Map<String, String> = unpackedEventFrom(message!!)

        val type = event["Event"]

        when (type) {
            "CLOSE" -> listener.auctionClosed()
            "PRICE" -> listener.currentPrice(event["CurrentPrice"]!!.toInt(), event["Increment"]!!.toInt())
            else -> println("Unknown event:$event")
        }
    }

    private fun unpackedEventFrom(message: Message): Map<String, String> {
        return message.body.split(";")
                .map { it.trim() }
                .map { it.split(":")[0].trim() to it.split(":")[1].trim() }
                .toMap()
    }
}
