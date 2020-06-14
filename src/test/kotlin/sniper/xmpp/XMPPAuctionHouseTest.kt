package sniper.xmpp

import org.assertj.core.api.Assertions.assertThat
import org.jivesoftware.smack.XMPPConnection
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import sniper.app.AuctionEventListener
import sniper.app.AuctionEventListener.PriceSource
import sniper.app.XMPPAuctionHouse
import sniper.app.XMPPAuctionHouse.Companion.AUCTION_RESOURCE
import test.app.FakeAuctionServer
import test.app.MainViewE2ETest.Companion.HOST_NAME
import test.app.MainViewE2ETest.Companion.SNIPER_ID
import test.app.MainViewE2ETest.Companion.SNIPER_PASSWORD
import test.app.MainViewE2ETest.Companion.SNIPER_XMPP_ID
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit.SECONDS

internal class XMPPAuctionHouseTest {
    private val server = FakeAuctionServer("item-54321")
    private val auctionHouse = XMPPAuctionHouse.connect(HOST_NAME, SNIPER_ID, SNIPER_PASSWORD)

    @BeforeEach fun startAuction() {
        server.startSailingItem()
    }

    @Test fun `receives events from auction server after joining`() {
        val auctionWasClosed = CountDownLatch(1)

        val auction = auctionHouse.auctionFor(server.itemId)
        auction.addAuctionEventListener(auctionClosedListener(auctionWasClosed))

        auction.join()
        server.hasReceivedJoinRequestFrom(SNIPER_XMPP_ID)
        server.announceClosed()

        assertThat(auctionWasClosed.await(2, SECONDS)).isTrue()
    }

    private fun auctionClosedListener(auctionWasClosed: CountDownLatch): AuctionEventListener {
        return object : AuctionEventListener {
            override fun auctionClosed() {
                auctionWasClosed.countDown()
            }

            override fun currentPrice(price: Int, increment: Int, priceSource: PriceSource) {
                TODO("Not yet implemented")
            }
        }
    }

    private fun connection(hostname: String, username: String, password: String): XMPPConnection {
        val connection = XMPPConnection(hostname)
        connection.connect()
        connection.login(username, password, AUCTION_RESOURCE)
        return connection
    }

    @AfterEach fun disconnect() {
        auctionHouse.disconnect()
    }
}
