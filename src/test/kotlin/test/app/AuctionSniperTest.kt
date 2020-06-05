package test.app

import com.danhaywood.java.assertjext.Conditions.matchedBy
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.FeatureMatcher
import org.hamcrest.Matcher
import org.junit.jupiter.api.Test
import sniper.app.Auction
import sniper.app.AuctionEventListener.PriceSource.FromOtherBidder
import sniper.app.AuctionEventListener.PriceSource.FromSniper
import sniper.app.AuctionSniper
import sniper.app.SniperListener
import sniper.app.SniperListener.SniperSnapshot
import sniper.app.SniperState
import sniper.app.SniperState.BIDDING
import test.app.AuctionSniperTest.SniperTestState.*


class AuctionSniperTest {

    private val auction: Auction = mockk(relaxUnitFun = true)
    private var sniperListener: SniperListener = mockk(relaxUnitFun = true)

    private var sniperState = idle;
    private val itemId = "item-xxxx"

    private var sniper = AuctionSniper(itemId, auction, sniperListener)

    @Test fun returnsLostWhenAuctionClosesImmediately() {
        sniper.auctionClosed()

        verify { sniperListener.sniperLost() }
    }

    @Test fun returnsLostWhenAuctionClosesWhenBidding() {
        every { sniperListener.sniperStateChanged(any()) } answers {
            sniperState = bidding
        }
        sniper.currentPrice(123, 45, FromOtherBidder)
        sniper.auctionClosed()

        verify { sniperListener.sniperLost() }
        assertThat(sniperState).isEqualTo(bidding)
    }

    @Test
    internal fun reportsWon_ifAuctionClosesWhenWinning() {
        every { sniperListener.sniperWinning() } answers {
            sniperState = winning
        }
        sniper.currentPrice(123, 45, FromSniper)
        sniper.auctionClosed()

        verify { sniperListener.sniperWon() }
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
        sniper.currentPrice(123, 45, FromOtherBidder)
        sniper.currentPrice(123, 45, FromSniper)

//        verify(sniperListener).sniperStateChanged(Mockito.argThat(SniperTharIs(BIDDING)))
//        verify(sniperListener).sniperStateChanged(MockitoHamcrest.argThat(aSniperTharIs(WINNING)))
    }

    private enum class SniperTestState {
        idle, winning, bidding
    }

    private open inner class SniperListenerStub : SniperListener {
        override fun sniperLost() {}
        override fun sniperWinning() {
            sniperState = winning
        }

        override fun sniperWon() {}

        override fun sniperStateChanged(snapshot: SniperSnapshot) {
            assertThat(snapshot).`is`(matchedBy(aSniperTharIs(BIDDING)))
            sniperState = bidding
        }
    }

    fun aSniperTharIs(state: SniperState): Matcher<SniperSnapshot> {
        return MyMatcher(state)
    }
}

class MyMatcher(state: SniperState) : FeatureMatcher<SniperSnapshot, SniperState>(equalTo(state), "sniper that is", "was") {
    override fun featureValueOf(actual: SniperSnapshot): SniperState {
        return actual.state
    }
}
