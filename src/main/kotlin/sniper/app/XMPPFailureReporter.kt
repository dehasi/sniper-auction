package sniper.app

interface XMPPFailureReporter {
    fun cannotTranslateMessage(auctionId: String, failedMessage: String, exception: Exception)
}
