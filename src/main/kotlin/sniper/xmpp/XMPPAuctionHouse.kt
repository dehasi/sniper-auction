package sniper.xmpp

import org.jivesoftware.smack.XMPPConnection
import sniper.app.AuctionHouse
import sniper.view.MainView

class XMPPAuctionHouse(private val connection: XMPPConnection) : AuctionHouse {

    override fun auctionFor(itemId: String) = XMPPAuction(connection, itemId)

    companion object {
        fun connect(hostname: String, username: String, password: String): AuctionHouse {
            val connection = XMPPConnection(hostname)
            connection.connect()
            connection.login(username, password, MainView.AUCTION_RESOURCE)
            return XMPPAuctionHouse(connection)
        }
    }
}
