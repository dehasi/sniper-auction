package test.app

import org.jivesoftware.smack.Chat
import org.jivesoftware.smack.packet.Message
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import sniper.app.AuctionEventListener
import sniper.app.AuctionEventListener.PriceSource.FromOtherBidder
import sniper.app.AuctionEventListener.PriceSource.FromSniper
import sniper.app.AuctionMessageTranslator

@ExtendWith(MockitoExtension::class)
class AuctionMessageTranslatorTest {

    companion object {
        val UNUSED_CHAT: Chat? = null
        val SNIPER_ID = "42"
    }

    @Mock private lateinit var listener: AuctionEventListener

    private lateinit var translator: AuctionMessageTranslator

    @BeforeEach fun createTranslator() {
        translator = AuctionMessageTranslator(SNIPER_ID, listener)
    }

    @Test fun notifiesAuctionClosedWhenCloseMessageReceived() {
        val message = Message()
        message.body = "SQLVersion: 1.1; Event: CLOSE"

        translator.processMessage(UNUSED_CHAT, message)

        verify(listener).auctionClosed()
    }

    @Test fun notifiesBidDetails_WhenCurrentPriceMessageReceived_FromOtherBidded() {
        val message = Message()
        message.body = "SQLVersion: 1.1; Event: PRICE; CurrentPrice: 192; Increment: 7; Bidder: someone else"

        translator.processMessage(UNUSED_CHAT, message)

        verify(listener).currentPrice(192, 7, FromOtherBidder)
    }

    @Test fun notifiesBidDetails_WhenCurrentPriceMessageReceived_FromSniper() {
        val message = Message()
        message.body = "SQLVersion: 1.1; Event: PRICE; CurrentPrice: 192; Increment: 7; Bidder: $SNIPER_ID"

        translator.processMessage(UNUSED_CHAT, message)

        verify(listener).currentPrice(192, 7, FromSniper)
    }
}
