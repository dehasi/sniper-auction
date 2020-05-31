package sniper.app

import org.jivesoftware.smack.Chat
import org.jivesoftware.smack.MessageListener
import org.jivesoftware.smack.packet.Message

open class AuctionMessageTranslator(private val listener: AuctionEventListener) : MessageListener {

    override fun processMessage(chat: Chat?, message: Message?) {
        val legacyEvent: Map<String, String> = unpackedEventFrom(message!!)
        val event = AuctionEvent.from(message.body)

        val type = event.type() //event["Event"]

        when (type) {
            "CLOSE" -> listener.auctionClosed()
            "PRICE" -> listener.currentPrice(event.currentPrice(), event.increment())
            else -> println("Unknown event:$event")
        }
    }

    private fun unpackedEventFrom(message: Message): Map<String, String> {
        return message.body.split(";")
                .map { it.trim() }
                .map { it.split(":")[0].trim() to it.split(":")[1].trim() }
                .toMap()
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

        companion object {
            fun from(messageBody: String): AuctionEvent {
                val event = AuctionEvent()
                for (field in fieldsIn(messageBody)) {
                    event.addField(field)
                }
                return event
            }

            fun fieldsIn(messageBody: String): List<String> {
                return messageBody.split(";")
            }
        }
    }
}
