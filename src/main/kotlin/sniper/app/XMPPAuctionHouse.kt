package sniper.app

import org.jivesoftware.smack.XMPPConnection
import sniper.xmpp.XMPPAuction
import java.util.logging.FileHandler
import java.util.logging.Handler
import java.util.logging.Logger
import java.util.logging.SimpleFormatter

class XMPPAuctionHouse(private val connection: XMPPConnection) : AuctionHouse {

    private val failureReporter:XMPPFailureReporter
    init {
        failureReporter = LoggingXMPPFailureReporter(makeLogger())
    }

    override fun auctionFor(itemId: String) = XMPPAuction(connection, itemId, failureReporter)

    override fun disconnect() {
        connection.disconnect()
    }

    private fun makeLogger(): Logger {
        val logger = Logger.getLogger(LOGGER_NAME)

        logger.useParentHandlers = false
        logger.addHandler(simpleFailHandler())
        return logger
    }

    private fun simpleFailHandler(): Handler {
        val handler = FileHandler(LOG_FILE_NAME)
        handler.formatter = SimpleFormatter()
        return handler
    }

    companion object {
        const val AUCTION_RESOURCE: String = "Auction"

        fun connect(hostname: String, username: String, password: String): AuctionHouse {
            val connection = XMPPConnection(hostname)
            connection.connect()
            connection.login(username, password, AUCTION_RESOURCE)
            return XMPPAuctionHouse(connection)
        }

        const val LOGGER_NAME = "auction-failture-logger"
        const val LOG_FILE_NAME = "auction-sniper.log"
    }
}
