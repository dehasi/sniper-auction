package sniper.eventhandling

import java.lang.reflect.Proxy
import java.util.*

class Announcer<LISTENER : EventListener>(type: Class<out LISTENER>) {

    private val listeners = mutableListOf<LISTENER>()
    private val proxy: LISTENER

    init {
        proxy = type.cast(Proxy.newProxyInstance(
                type.classLoader, arrayOf<Class<*>>(type)

        ){any, method, arrayOfAnys ->null

        })
    }

    fun announce(): LISTENER {
        return proxy
    }

    fun addListener(listener: LISTENER) {
        listeners.add(listener)
    }

    companion object {
        fun <LISTENER : EventListener> `for`(type: Class<out LISTENER>): Announcer<LISTENER> = Announcer(type)
    }
}
