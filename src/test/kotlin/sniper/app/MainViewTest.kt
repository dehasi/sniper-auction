package sniper.app

import javafx.scene.Scene
import javafx.stage.Stage
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.api.FxAssert.verifyThat
import org.testfx.framework.junit5.ApplicationExtension
import org.testfx.framework.junit5.Start
import org.testfx.matcher.control.LabeledMatchers.hasText
import sniper.view.MainView
import tornadofx.*

@ExtendWith(ApplicationExtension::class)
class MainViewTest {
    private val HOST_NAME: String = "name-of-host"
    private val SNIPER_ID: String = "sniper"
    private val SNIPER_PASSWORD: String = "password"

    private val auction: FakeAuctionServer = FakeAuctionServer("item-54321")

    @Start fun biddingIn(stage: Stage) {
        auction.startSailingItem()
        startBiddingIn(stage, auction)
    }

    @Test fun sniperJoinsAuction_until_AuctionCloses() {
        auction.hasReceivedJoinRequestFromSniper()
        auction.announceClosed()
        showsSniperHasLostAuction()
    }

    private fun startBiddingIn(stage: Stage, auction: FakeAuctionServer) {
        val data = Data(HOST_NAME, SNIPER_ID, SNIPER_PASSWORD, auction.itemId)
        setInScope(data, kclass = Data::class)

        val view = MainView()

        stage.scene = Scene(view.root)
        stage.show()

        verifyThat("#main-label", hasText(HOST_NAME))
    }

    private fun showsSniperHasLostAuction() {
        verifyThat("#main-label", hasText(HOST_NAME))
    }
}
