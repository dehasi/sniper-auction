package test.app

import io.mockk.MockKVerificationScope
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import sniper.app.Auction
import sniper.app.AuctionEventListener.PriceSource.FromOtherBidder
import sniper.app.AuctionEventListener.PriceSource.FromSniper
import sniper.app.AuctionSniper
import sniper.app.SniperListener
import sniper.app.SniperListener.SniperSnapshot
import sniper.app.SniperState
import sniper.app.SniperState.*
import test.app.AuctionSniperTest.SniperTestState.*


class AuctionSniperTest {

    private val auction: Auction = mockk(relaxUnitFun = true)
    private var sniperListener: SniperListener = mockk(relaxUnitFun = true)

    private var sniperState = idle;
    private val itemId = "item-xxxx"

    private var sniper = AuctionSniper(itemId, auction, sniperListener)

    @Test fun returnsLostWhenAuctionClosesImmediately() {
        sniper.auctionClosed()

        verify { sniperListener.sniperStateChanged(aSniperTharIs(LOST)) }
    }

    @Test fun returnsLostWhenAuctionClosesWhenBidding() {
        every { sniperListener.sniperStateChanged(any()) } answers {
            sniperState = bidding
        }
        sniper.currentPrice(123, 45, FromOtherBidder)
        sniper.auctionClosed()

        verify { sniperListener.sniperStateChanged(SniperSnapshot(itemId, 123, 168, LOST)) }
        assertThat(sniperState).isEqualTo(bidding)
    }

    @Test
    internal fun reportsWon_ifAuctionClosesWhenWinning() {
        every { sniperListener.sniperStateChanged(any()) } answers {
            sniperState = winning
        }
        sniper.currentPrice(123, 45, FromSniper)
        sniper.auctionClosed()

        verify { sniperListener.sniperStateChanged(aSniperTharIs(WON)) }
        assertThat(sniperState).isEqualTo(winning)
    }

    @Test internal fun bidsHigherAndReportsBiddingWhenNewPriceArrives() {
        val price = 1001
        val increment = 25
        val bid = price + increment

        sniper.currentPrice(price, increment, FromOtherBidder)

        verify {
            sniperListener.sniperStateChanged(SniperSnapshot(itemId, price, bid, BIDDING))
        }
    }

    @Test internal fun reportsIsWinning_whenCurrentPriceComesFromSniper() {
        sniper.currentPrice(123, 12, FromOtherBidder)
        sniper.currentPrice(135, 45, FromSniper)

        verify {
            sniperListener.sniperStateChanged(SniperSnapshot(itemId, 123, 135, BIDDING))
            sniperListener.sniperStateChanged(SniperSnapshot(itemId, 135, 135, WINNING))
        }
    }

    private enum class SniperTestState {
        idle, winning, bidding
    }
}

fun MockKVerificationScope.aSniperTharIs(state: SniperState) = match<SniperSnapshot> {
    it.state == state
}
