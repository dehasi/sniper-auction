package test.app

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import sniper.app.AuctionSniper
import sniper.app.SniperListener

@ExtendWith(MockitoExtension::class)
class AuctionSniperTest {

    @Mock private lateinit var sniperListener: SniperListener
    @InjectMocks private lateinit var sniper: AuctionSniper

    @Test fun returnsLostWhenAuctionCloses() {
        sniper.auctionClosed()

        verify(sniperListener).sniperLost()
    }
}
