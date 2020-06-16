package sniper.app

import sniper.eventhandling.Announcer
import java.util.*

class SniperPortfolio: SniperCollector {

    private val snipers = ArrayList<AuctionSniper>()
    private val announcer = Announcer.to(PortfolioListener::class.java)

    override fun addSniper(sniper: AuctionSniper) {
        snipers.add(sniper)
        announcer.announce().sniperAdded(sniper)
    }

    fun addPortfolioListener(listener: PortfolioListener) {
        announcer.addListener(listener)
    }

    interface PortfolioListener : EventListener {
        fun sniperAdded(sniper: AuctionSniper)
    }
}
