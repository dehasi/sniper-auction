package sniper.app

enum class SniperState {

    JOINING {
        override fun whenAuctionClosed(): SniperState = LOST
    },
    BIDDING {
        override fun whenAuctionClosed(): SniperState = LOST
    },
    WINNING {
        override fun whenAuctionClosed(): SniperState = WON
    },
    LOSING {
        override fun whenAuctionClosed(): SniperState = LOST
    },
    LOST,
    WON;

    open fun whenAuctionClosed(): SniperState {
        TODO("Auction is already closed")
    }
}
