package sniper.app

import org.jivesoftware.smack.XMPPConnection
import sniper.xmpp.XMPPAuction

class XMPPAuctionHouse(private val connection: XMPPConnection) : AuctionHouse {

    override fun auctionFor(itemId: String) = XMPPAuction(connection, itemId)

    override fun disconnect() {
        connection.disconnect()
    }

    companion object {
        const val AUCTION_RESOURCE: String = "Auction"

        fun connect(hostname: String, username: String, password: String): AuctionHouse {
            val connection = XMPPConnection(hostname)
            connection.connect()
            connection.login(username, password, AUCTION_RESOURCE)
            return XMPPAuctionHouse(connection)
        }

    }
}
