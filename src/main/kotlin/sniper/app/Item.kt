package sniper.app

data class Item(val identifier: String, val stopPrice: Int) {
    fun allowsBid(bid: Int) = bid < stopPrice
}
