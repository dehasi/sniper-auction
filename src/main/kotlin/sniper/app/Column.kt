package sniper.app

enum class Column {
    ITEM_IDENTIFIER,
    LAST_PRICE,
    LAST_BID,
    SNIPER_STATE;

    fun at(offset: Int): Column {
        return values()[offset]
    }
}
