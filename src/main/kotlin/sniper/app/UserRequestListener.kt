package sniper.app

import java.util.*

interface UserRequestListener : EventListener {
    fun joinAuction(item: Item)
}
