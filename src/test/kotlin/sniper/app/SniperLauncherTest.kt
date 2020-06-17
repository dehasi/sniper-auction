package sniper.app

import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


internal class SniperLauncherTest {

    private val auctionHouse: AuctionHouse = mockk()
    private val sniperCollector: SniperCollector = mockk()
    private val launcher = SniperLauncher(auctionHouse, sniperCollector)

    private val stateNotJoined = "not joined"
    private val stateJoined = "joined"

    @Test fun `add a new sniper to collector and then join an auction`() {
        val item = Item("item 123",123)
        var state = stateNotJoined
        val auctionStub = object : Auction {
            override fun join() {
                state = stateJoined
            }

            override fun bid(amount: Int) {
                TODO("Isn't called")
            }

            override fun addAuctionEventListener(auctionEventListener: AuctionEventListener) {
                assertThat(state).isEqualTo(stateNotJoined)
            }
        }
        every { auctionHouse.auctionFor(item.identifier) } returns auctionStub
        every { sniperCollector.addSniper(any()) } answers {
            assertThat(state).isEqualTo(stateNotJoined)
        }
        launcher.joinAuction(item)

        assertThat(state).isEqualTo(stateJoined)
    }
}
