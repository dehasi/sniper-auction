package test.app

import javafx.scene.Scene
import javafx.stage.Stage
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.api.FxAssert.verifyThat
import org.testfx.framework.junit5.ApplicationExtension
import org.testfx.framework.junit5.Start
import org.testfx.matcher.control.LabeledMatchers.hasText
import org.testfx.util.WaitForAsyncUtils.sleep
import sniper.app.Data
import sniper.view.MainView
import tornadofx.*
import java.util.concurrent.TimeUnit.MILLISECONDS

@ExtendWith(ApplicationExtension::class)
class MainViewTest {
    companion object {
        private const val HOST_NAME: String = "localhost"
        private const val SNIPER_ID: String = "sniper"
        private const val SNIPER_PASSWORD: String = "sniper"
        private const val SNIPER_XMPP_ID: String = "sniper@lc/Auction" // check Openfire why not localhost
    }

    private val auction: FakeAuctionServer = FakeAuctionServer("item-54321")

    @Start fun biddingIn(stage: Stage) {
        auction.startSailingItem()
        startBiddingIn(stage, auction)
    }

    @Test fun sniperMakesAHighestBid_butLoses() {
        auction.hasReceivedJoinRequestFrom(SNIPER_XMPP_ID)

        auction.reportPrice(1000, 98, "other bidder")
        hasShownSniperIsBidding()

        auction.hasReceivedBid(1098, SNIPER_XMPP_ID)

        auction.announceClosed()
        showsSniperHasLostAuction()
    }

    private fun startBiddingIn(stage: Stage, auction: FakeAuctionServer) {
        val data = Data(HOST_NAME, SNIPER_ID, SNIPER_PASSWORD, auction.itemId)
        setInScope(data, kclass = Data::class)

        val view = MainView()

        stage.scene = Scene(view.root)
        stage.show()

        verifyThat("#main-label", hasText("Joining"))
    }

    private fun hasShownSniperIsBidding() {
        verifyThat("#main-label", hasText("Bidding"))
    }

    private fun showsSniperHasLostAuction() {
        sleep(6, MILLISECONDS)
        verifyThat("#main-label", hasText("Lost"))
    }

    @AfterEach fun stopAuction() {
        auction.stop()
    }
    // [x] Single item - join, lose without bidding
    // [_] Single item - join, bid & lose
    // [_] Single item - join, bid & win
    // [_] Single item - show price details
    // [_] Multiple items
    // [_] Add new items through the GUI
    // [_] Stop bidding at stop price
    // [_] Translator - invalid message from Auction
    // [_] Translator - incorrect message version
}