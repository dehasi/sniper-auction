package sniper.xmpp

import io.mockk.mockk
import io.mockk.verify
import org.jivesoftware.smack.Chat
import org.jivesoftware.smack.packet.Message
import org.junit.jupiter.api.Test
import sniper.app.AuctionEventListener
import sniper.app.AuctionEventListener.PriceSource.FromOtherBidder
import sniper.app.AuctionEventListener.PriceSource.FromSniper

internal class AuctionMessageTranslatorTest {
    companion object {
        const val SNIPER_ID = "42"
        val UNUSED_CHAT: Chat? = null
    }

    private val listener: AuctionEventListener = mockk(relaxUnitFun = true)
    private val translator: AuctionMessageTranslator = AuctionMessageTranslator(SNIPER_ID, listener)

    @Test fun notifiesAuctionClosedWhenCloseMessageReceived() {
        val message = Message()
        message.body = "SOLVersion: 1.1; Event: CLOSE"

        translator.processMessage(UNUSED_CHAT, message)

        verify {
            listener.auctionClosed()
        }
    }

    @Test fun notifiesBidDetails_WhenCurrentPriceMessageReceived_FromOtherBidder() {
        val message = Message()
        message.body = "SOLVersion: 1.1; Event: PRICE; CurrentPrice: 192; Increment: 7; Bidder: someone else"

        translator.processMessage(UNUSED_CHAT, message)

        verify {
            listener.currentPrice(192, 7, FromOtherBidder)
        }
    }

    @Test fun notifiesBidDetails_WhenCurrentPriceMessageReceived_FromSniper() {
        val message = Message()
        message.body = "SOLVersion: 1.1; Event: PRICE; CurrentPrice: 192; Increment: 7; Bidder: $SNIPER_ID"

        translator.processMessage(UNUSED_CHAT, message)

        verify { listener.currentPrice(192, 7, FromSniper) }
    }
}
