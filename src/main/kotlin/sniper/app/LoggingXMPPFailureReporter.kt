package sniper.app

import java.util.logging.Logger

class LoggingXMPPFailureReporter(private val logger: Logger) : XMPPFailureReporter {
    override fun cannotTranslateMessage(auctionId: String, failedMessage: String, exception: Exception) {
        logger.severe("<$auctionId> Could not translate message \"$failedMessage\" because $exception")
    }
}
