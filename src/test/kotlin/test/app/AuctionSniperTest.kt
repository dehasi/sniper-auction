package test.app

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.spy
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import sniper.app.Auction
import sniper.app.AuctionEventListener.PriceSource.FromOtherBidder
import sniper.app.AuctionEventListener.PriceSource.FromSniper
import sniper.app.AuctionSniper
import sniper.app.SniperListener
import sniper.app.SniperSnapshot
import sniper.app.SniperState.BIDDING
import test.app.AuctionSniperTest.SniperState.*

@ExtendWith(MockitoExtension::class)
class AuctionSniperTest {

    @Mock private lateinit var auction: Auction

    private var sniperState = idle;
    private var sniperListener: SniperListener = spy(SniperListenerStub())
    private val itemId = "item-xxxx"

    private lateinit var sniper: AuctionSniper

    @BeforeEach fun createSniper() {
        sniper = AuctionSniper(itemId, auction, sniperListener)
    }

    @Test fun returnsLostWhenAuctionClosesImmediately() {
        sniper.auctionClosed()

        verify(sniperListener).sniperLost()
    }

    @Test fun returnsLostWhenAuctionClosesWhenBidding() {
        sniper.currentPrice(123, 45, FromOtherBidder)
        sniper.auctionClosed()

        verify(sniperListener).sniperLost()
        assertThat(sniperState).isEqualTo(bidding)
    }

    @Test
    internal fun reportsWon_ifAuctionClosesWhenWinning() {
        sniper.currentPrice(123, 45, FromSniper)
        sniper.auctionClosed()

        verify(sniperListener).sniperWon()
        assertThat(sniperState).isEqualTo(winning)
    }

    @Test internal fun bidsHigherAndReportsBiddingWhenNewPriceArrives() {
        val price = 1001
        val increment = 25
        val bid = price + increment

        sniper.currentPrice(price, increment, FromOtherBidder)

        verify(sniperListener).sniperBidding(SniperSnapshot(itemId, price, bid, BIDDING))
    }

    @Test internal fun reportsIsWinning_whenCurrentPriceComesFromSniper() {
        sniper.currentPrice(123, 45, FromSniper)

        verify(sniperListener).sniperWinning()
    }

    private enum class SniperState {
        idle, winning, bidding
    }

    private open inner class SniperListenerStub : SniperListener {
        override fun sniperLost() {}
        override fun sniperWinning() {
            sniperState = winning
        }

        override fun sniperWon() {}

        override fun sniperBidding(snapshot: SniperSnapshot) {
            sniperState = bidding
        }
    }
}
