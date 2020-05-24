package sniper.app

class FakeAuctionServer(val itemId: String) {
    fun startSailingItem() {
        print("Start sailing $itemId")
    }

    fun hasReceivedJoinRequestFromSniper() {
        // TODO("Not yet implemented")
    }

    fun announceClosed() {
        // TODO("Not yet implemented")
    }
}
