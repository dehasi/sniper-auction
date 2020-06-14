package sniper.eventhandling

import java.lang.reflect.Method
import java.lang.reflect.Proxy.newProxyInstance
import java.util.*

class Announcer<LISTENER : EventListener>(type: Class<out LISTENER>) {

    private val listeners = mutableListOf<LISTENER>()
    private val proxy: LISTENER

    init {
        proxy = type.cast(newProxyInstance(type.classLoader, arrayOf<Class<*>>(type))
        { _, method, args ->
            announce(method, args ?: emptyArray())
            null
        })
    }

    fun addListener(listener: LISTENER) = listeners.add(listener)

    fun announce(): LISTENER = proxy

    private fun announce(method: Method, args: Array<Any>) {
        listeners.forEach {
            method.invoke(it, *args)
        }
    }

    companion object {
        fun <LISTENER : EventListener> `to`(type: Class<out LISTENER>): Announcer<LISTENER> = Announcer(type)
    }
}
