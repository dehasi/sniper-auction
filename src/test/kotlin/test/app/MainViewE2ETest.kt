package test.app

import javafx.scene.Scene
import javafx.stage.Stage
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.api.FxAssert.verifyThat
import org.testfx.framework.junit5.ApplicationExtension
import org.testfx.framework.junit5.Start
import org.testfx.matcher.control.TableViewMatchers.containsRow
import org.testfx.util.WaitForAsyncUtils.sleep
import sniper.app.Data
import sniper.view.MainView
import sniper.view.MainView.Companion.BIDDING
import sniper.view.MainView.Companion.JOINING
import sniper.view.MainView.Companion.WINNING
import sniper.view.MainView.Companion.WON
import tornadofx.*
import java.util.concurrent.TimeUnit.MILLISECONDS

@ExtendWith(ApplicationExtension::class)
class MainViewE2ETest {
    companion object {
        private const val HOST_NAME: String = "localhost"
        private const val SNIPER_ID: String = "sniper"
        private const val SNIPER_PASSWORD: String = "sniper"
        private const val SNIPER_XMPP_ID: String = "sniper@localhost/Auction"
    }

    private val auction: FakeAuctionServer = FakeAuctionServer("item-54321")

    private lateinit var itemId: String

    @Start fun biddingIn(stage: Stage) {
        itemId = auction.itemId

        auction.startSailingItem()
        startBiddingIn(stage, auction)
    }

    @Test fun `sniper makes a highest bid but loses`() {
        auction.hasReceivedJoinRequestFrom(SNIPER_XMPP_ID)

        auction.reportPrice(1000, 98, "other bidder")
        hasShownSniperIsBidding(1000, 1098)

        auction.hasReceivedBid(1098, SNIPER_XMPP_ID)

        auction.announceClosed()
        showsSniperHasLostAuction()
    }

    @Test fun `sniper wins an auction by bidding higher`() {
        auction.hasReceivedJoinRequestFrom(SNIPER_XMPP_ID)

        auction.reportPrice(1000, 98, "other bidder")
        hasShownSniperIsBidding(1000, 1098)

        auction.hasReceivedBid(1098, SNIPER_XMPP_ID)

        auction.reportPrice(1098, 97, SNIPER_XMPP_ID)
        hasShownSniperIsWinning(1098)

        auction.announceClosed()
        showsSniperHasWonAuction(1098)
    }

    private fun startBiddingIn(stage: Stage, auction: FakeAuctionServer) {
        val data = Data(HOST_NAME, SNIPER_ID, SNIPER_PASSWORD, auction.itemId)
        setInScope(data, kclass = Data::class)

        val view = MainView()

        stage.scene = Scene(view.root)
        stage.show()

        showsSniperStatus("", 0, 0, JOINING)
    }

    private fun hasShownSniperIsBidding(lastPrice: Int, lastBid: Int) {
        sleep(200, MILLISECONDS)
        showsSniperStatus(itemId, lastPrice, lastBid, BIDDING)
    }

    private fun showsSniperHasLostAuction() {
        sleep(200, MILLISECONDS)
        showsSniperStatus(itemId, 0, 0, "Lost")
    }

    private fun hasShownSniperIsWinning(winningBid: Int) {
        sleep(200, MILLISECONDS)
        verifyThat("#snipers-table", containsRow(WINNING))
        showsSniperStatus(itemId, winningBid, winningBid, WINNING)
    }

    private fun showsSniperHasWonAuction(lastPrice: Int) {
        sleep(200, MILLISECONDS)
        verifyThat("#snipers-table", containsRow(WON))
        showsSniperStatus(itemId, lastPrice, lastPrice, WON)
    }

    private fun showsSniperStatus(itemId: String, lastPrice: Int, lastBid: Int, status: String) {
        verifyThat("#snipers-table", containsRow(itemId, lastPrice, lastBid, status))
    }

    @AfterEach fun stopAuction() {
        auction.stop()
    }
    // [x] Single item - join, lose without bidding
    // [x] Single item - join, bid & lose
    // [x] Single item - join, bid & win
    // [_] Single item - show price details
    // [_] Multiple items
    // [_] Add new items through the GUI
    // [_] Stop bidding at stop price
    // [_] Translator - invalid message from Auction
    // [_] Translator - incorrect message version
    // [_] Auction - handle XMPPException on send
    // [_] Experiment with testcontainers instead of local XMPP Server
    // [_] Experiment with kotest
    // [_] Extract statuses to constants
    // [_] try MockK instead of Mockito
}
