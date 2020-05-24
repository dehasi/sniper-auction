package sniper.app

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
import sniper.view.MainView
import tornadofx.*
import java.util.concurrent.TimeUnit.MILLISECONDS

@ExtendWith(ApplicationExtension::class)
class MainViewTest {
    private val HOST_NAME: String = "localhost"
    private val SNIPER_ID: String = "sniper"
    private val SNIPER_PASSWORD: String = "sniper"

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

        verifyThat("#main-label", hasText("Joining"))
    }

    private fun showsSniperHasLostAuction() {
        sleep(6, MILLISECONDS)
        verifyThat("#main-label", hasText("Lost"))
    }

    @AfterEach fun stopAuction() {
        auction.stop()
    }
}
