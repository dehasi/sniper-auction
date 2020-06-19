package sniper.xmpp

import org.jivesoftware.smack.Chat
import org.jivesoftware.smack.MessageListener
import org.jivesoftware.smack.packet.Message
import sniper.app.AuctionEventListener
import sniper.app.AuctionEventListener.PriceSource.FromOtherBidder
import sniper.app.AuctionEventListener.PriceSource.FromSniper

internal class AuctionMessageTranslator(
        private val sniperId: String,
        private val listener: AuctionEventListener) : MessageListener {

    override fun processMessage(chat: Chat?, message: Message?) {
        try {
            translate(message!!.body)
        } catch (e: Exception) {
            listener.auctionFailed()
        }
    }

    private fun translate(messageBody: String) {
        val event = AuctionEvent.from(messageBody)

        when (event.type()) {
            "CLOSE" -> listener.auctionClosed()
            "PRICE" -> listener.currentPrice(event.currentPrice(), event.increment(), event.isFrom(sniperId))
            else -> println("Unknown event:$event")
        }
    }

    private class AuctionEvent {
        private var fields = mutableMapOf<String, String>()

        fun type(): String {
            return get("Event")
        }

        fun currentPrice(): Int {
            return getInt("CurrentPrice")
        }

        fun increment(): Int {
            return getInt("Increment")
        }

        fun isFrom(sniperId: String): AuctionEventListener.PriceSource {
            return if (bidder() == sniperId) FromSniper else FromOtherBidder
        }

        private fun getInt(fieldName: String): Int {
            return get(fieldName).toInt()
        }

        private fun get(fieldName: String): String {
            return fields[fieldName]!!
        }

        private fun addField(field: String) {
            val pair = field.split(":")
            fields[pair[0].trim()] = pair[1].trim()
        }

        private fun bidder() = get("Bidder")

        companion object {
            fun from(messageBody: String): AuctionEvent {
                val event = AuctionEvent()
                fieldsIn(messageBody).forEach { event.addField(it) }
                return event
            }

            fun fieldsIn(messageBody: String): List<String> {
                return messageBody.split(";")
            }
        }
    }
}
