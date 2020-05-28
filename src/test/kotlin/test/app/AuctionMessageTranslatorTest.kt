package test.app

import org.jivesoftware.smack.Chat
import org.jivesoftware.smack.packet.Message
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import sniper.app.AuctionEventListener
import sniper.app.AuctionMessageTranslator

@ExtendWith(MockitoExtension::class)
class AuctionMessageTranslatorTest {

    companion object {
        val UNUSED_CHAT: Chat? = null
    }

    @Mock
    lateinit var listener: AuctionEventListener

    private val translator: AuctionMessageTranslator = AuctionMessageTranslator()

    @Test fun notifiesAuctionClosedWhenCloseMessageReceived() {
        val message = Message()
        message.body = "SQLVersion: 1.1; Event: CLOSE;"

        translator.processMessage(UNUSED_CHAT, message)

        verify(listener).auctionClosed()
    }
}
