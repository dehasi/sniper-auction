package test.app

import javafx.scene.Scene
import javafx.scene.control.TextField
import javafx.stage.Stage
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.api.FxAssert.verifyThat
import org.testfx.api.FxRobot
import org.testfx.framework.junit5.ApplicationExtension
import org.testfx.framework.junit5.Start
import org.testfx.matcher.control.TableViewMatchers.containsRowAtIndex
import org.testfx.util.WaitForAsyncUtils.sleep
import sniper.app.Data
import sniper.app.SniperState.JOINING
import sniper.view.MainView
import sniper.view.SniperStateData.Companion.STATUS_BIDDING
import sniper.view.SniperStateData.Companion.STATUS_FAILED
import sniper.view.SniperStateData.Companion.STATUS_LOSING
import sniper.view.SniperStateData.Companion.STATUS_LOST
import sniper.view.SniperStateData.Companion.STATUS_WINNING
import sniper.view.SniperStateData.Companion.STATUS_WON
import sniper.view.SniperStateData.Companion.textFor
import tornadofx.*
import java.util.concurrent.TimeUnit.MILLISECONDS

@ExtendWith(ApplicationExtension::class)
class MainViewE2ETest {
    companion object {
        const val HOST_NAME: String = "localhost"
        const val SNIPER_ID: String = "sniper"
        const val SNIPER_PASSWORD: String = "sniper"
        const val SNIPER_XMPP_ID: String = "sniper@localhost/Auction"
    }

    private val auction: FakeAuctionServer = FakeAuctionServer("item-54321")
    private val auction2: FakeAuctionServer = FakeAuctionServer("item-65432")
    private val logDriver = AuctionLogDriver()

    private val auctions = listOf(auction, auction2)
    private val itemRow = mutableMapOf<String, Int>()

    @Start fun biddingIn(stage: Stage) {
        auction.startSailingItem()
        auction2.startSailingItem()
        createApp(stage, auction, auction2)
    }

    @Test fun `sniper bids for multiple items`(robot: FxRobot) {
        startBiddingInWithStopPrice(robot, 2000, auctions)

        auction.hasReceivedJoinRequestFrom(SNIPER_XMPP_ID)
        auction2.hasReceivedJoinRequestFrom(SNIPER_XMPP_ID)

        auction.reportPrice(1000, 98, "other bidder")
        hasShownSniperIsBidding(auction, 1000, 1098)
        auction.hasReceivedBid(1098, SNIPER_XMPP_ID)


        auction2.reportPrice(500, 21, "other bidder")
        hasShownSniperIsBidding(auction2, 500, 521)
        auction2.hasReceivedBid(521, SNIPER_XMPP_ID)

        auction.reportPrice(1098, 97, SNIPER_XMPP_ID)
        hasShownSniperIsWinning(auction, 1098)

        auction2.reportPrice(521, 22, SNIPER_XMPP_ID)
        hasShownSniperIsWinning(auction2, 521)

        auction.announceClosed()
        auction2.announceClosed()

        showsSniperHasWonAuction(auction, 1098)
        showsSniperHasWonAuction(auction2, 521)
    }

    @Test fun `sniper loses an auction when the price is too high`(robot: FxRobot) {
        startBiddingInWithStopPrice(robot, 1100, listOf(auction))

        auction.hasReceivedJoinRequestFrom(SNIPER_XMPP_ID)
        auction.reportPrice(1000, 98, "other bidder")
        hasShownSniperIsBidding(auction, 1000, 1098)
        auction.hasReceivedBid(1098, SNIPER_XMPP_ID)

        auction.reportPrice(1197, 10, "third party")
        hasShownSniperIsLosing(auction, 1197, 1098)

        auction.reportPrice(1207, 10, "fourth party")
        hasShownSniperIsLosing(auction, 1207, 1098)

        auction.announceClosed()
        showsSniperHasLostAuction(auction, 1207, 1098)
    }

    @Test fun `sniper report invalid auction message and stops responding to events`(robot: FxRobot) {
        val brokenMessage = "a broken message"
        startBiddingInWithStopPrice(robot, 2000, auctions)
        auction.hasReceivedJoinRequestFrom(SNIPER_XMPP_ID)

        auction.reportPrice(500, 20, "other bidder")
        auction.hasReceivedBid(520, SNIPER_XMPP_ID)

        auction.sendInvalidMessageContaining(brokenMessage)
        showsSniperHasFailed(robot, auction)

        auction.reportPrice(520, 21, "other bidder")
        waitForAnotherEvent()

        reportsInvalidMessage(robot, auction, brokenMessage)
        showsSniperHasFailed(robot, auction)
    }

    private fun reportsInvalidMessage(robot: FxRobot, auction: FakeAuctionServer, message: String) {
        logDriver.hasEntry { it.contains(message) }
    }

    private fun waitForAnotherEvent() {
        auction2.hasReceivedJoinRequestFrom(SNIPER_XMPP_ID)
        auction2.reportPrice(600, 6, "other bidder")
        hasShownSniperIsBidding(auction2, 600, 606)
    }

    private fun startBiddingInWithStopPrice(robot: FxRobot, stopPrice: Int, auctions: List<FakeAuctionServer>) {
        auctions.forEach {
            startBiddingInFor(robot, it.itemId, stopPrice)
            showsSniperStatus(it.itemId, 0, 0, textFor(JOINING))
        }
    }

    private fun startBiddingInFor(robot: FxRobot, itemId: String, stopPrice: Int) {
        robot.lookup("#item-textbox").query<TextField>().text = itemId
        robot.lookup("#stop-price-textbox").query<TextField>().text = stopPrice.toString()
        robot.clickOn("#bid-button")
    }

    private fun createApp(stage: Stage, vararg auctions: FakeAuctionServer) {
        val items = auctions.map { it.itemId }
        for (index in items.indices) {
            itemRow[items[index]] = index
        }

        val data = Data(HOST_NAME, SNIPER_ID, SNIPER_PASSWORD, items)
        setInScope(data, kclass = Data::class)

        val view = MainView()

        stage.scene = Scene(view.root)
        stage.show()
    }

    private fun hasShownSniperIsBidding(auction: FakeAuctionServer, lastPrice: Int, lastBid: Int) {
        sleep(200, MILLISECONDS)
        showsSniperStatus(auction.itemId, lastPrice, lastBid, STATUS_BIDDING)
    }

    private fun showsSniperHasLostAuction(auction: FakeAuctionServer, lastPrice: Int, lastBid: Int) {
        sleep(200, MILLISECONDS)
        showsSniperStatus(auction.itemId, lastPrice, lastBid, STATUS_LOST)
    }

    private fun hasShownSniperIsLosing(auction: FakeAuctionServer, lastPrice: Int, lastBid: Int) {
        sleep(200, MILLISECONDS)
        showsSniperStatus(auction.itemId, lastPrice, lastBid, STATUS_LOSING)
    }

    private fun hasShownSniperIsWinning(auction: FakeAuctionServer, winningBid: Int) {
        sleep(200, MILLISECONDS)
        showsSniperStatus(auction.itemId, winningBid, winningBid, STATUS_WINNING)
    }

    private fun showsSniperHasWonAuction(auction: FakeAuctionServer, lastPrice: Int) {
        sleep(200, MILLISECONDS)
        showsSniperStatus(auction.itemId, lastPrice, lastPrice, STATUS_WON)
    }

    private fun showsSniperHasFailed(robot: FxRobot, auction: FakeAuctionServer) {
        sleep(200, MILLISECONDS)
        showsSniperStatus(auction.itemId, 0, 0, STATUS_FAILED)
    }

    private fun showsSniperStatus(itemId: String, lastPrice: Int, lastBid: Int, status: String) {
        verifyThat("#snipers-table", containsRowAtIndex(itemRow[itemId]!!, itemId, lastPrice, lastBid, status))
    }

    @AfterEach fun stopAuction() {
        auction.stop()
        logDriver.clearLog()
    }

    @AfterEach fun clearLog() {
        logDriver.clearLog()
    }
    // [x] Single item - join, lose without bidding
    // [x] Single item - join, bid & lose
    // [x] Single item - join, bid & win
    // [x] Single item - show price details
    // [x] Multiple items
    // [x] Add new items through the GUI
    // [x] Stop bidding at stop price
    // [x] Translator - invalid message from Auction
    // [_] Translator - incorrect message version
    // [_] Auction - handle XMPPException on send
    // [_] Experiment with testcontainers instead of local XMPP Server
    // [_] Experiment with kotest
    // [x] Extract statuses to constants
    // [x] Use MockK instead of Mockito
    // [x] Create a custom matcher to check column names for SnipersTableModelTest
}
