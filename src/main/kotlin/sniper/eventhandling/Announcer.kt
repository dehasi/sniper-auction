package sniper.eventhandling

import java.util.*

class Announcer<LISTENER : EventListener>(type: Class<out LISTENER>) {

    private val listeners = mutableListOf<LISTENER>()

    fun announce(): LISTENER {
        TODO()
    }

    fun addListener(listener: LISTENER) {
        listeners.add(listener)
    }

    companion object {
        fun <LISTENER : EventListener> `for`(type: Class<out LISTENER>): Announcer<LISTENER> = Announcer(type)
    }
}
