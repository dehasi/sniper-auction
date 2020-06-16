package sniper.app

import io.mockk.mockk
import org.junit.jupiter.api.Test
import sniper.view.SnipersTableModel


internal class SniperLauncherTest {

    private val auctionHouse: AuctionHouse = mockk()
    private val snipers: SnipersTableModel= mockk()
    private val launcher = SniperLauncher(auctionHouse, snipers)

    @Test fun `add a new sniper to collector and then join an auction`() {
        val itemId = "item 123"

        launcher.joinAuction(itemId)
    }
}
