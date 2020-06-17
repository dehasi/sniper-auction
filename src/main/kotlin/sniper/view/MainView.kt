package sniper.view

import sniper.app.*
import sniper.eventhandling.Announcer
import tornadofx.*

class MainView : View("Auction Sniper") {

    private val data: Data by inject()
    private val portfolio = SniperPortfolio()

    private val userRequests = Announcer.to(UserRequestListener::class.java)

    private val auctionHouse: AuctionHouse

    override val root = vbox {
        hbox {
            val textfield = textfield {
                id = "item-textbox"
            }
            button("Join Auction") {
                id = "bid-button"
                action {
                    userRequests.announce().joinAuction(textfield.text)
                }
            }
        }
        this += makeSnipersTable(portfolio)
    }

    init {
        auctionHouse = XMPPAuctionHouse.connect(data.hostname, data.username, data.password)
        disconnectWhenUICloses(auctionHouse)
        addUserRequestListenerFor(auctionHouse)
    }

    private fun makeSnipersTable(portfolio: SniperPortfolio): SnipersTableModel {
        val model = SnipersTableModel()
        portfolio.addPortfolioListener(model)
        return model
    }

    private fun addUserRequestListenerFor(auctionHouse: AuctionHouse) {
        addUserRequestListener(SniperLauncher(auctionHouse, portfolio))
    }

    private fun disconnectWhenUICloses(auctionHouse: AuctionHouse) {
        // TODO call auctionHouse.disconnect()
    }

    fun addUserRequestListener(userRequestListener: UserRequestListener) {
        userRequests.addListener(userRequestListener)
    }
}

