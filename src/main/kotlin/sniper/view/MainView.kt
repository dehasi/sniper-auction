package sniper.view

import javafx.application.Platform
import javafx.beans.property.SimpleStringProperty
import org.jivesoftware.smack.Chat
import org.jivesoftware.smack.MessageListener
import org.jivesoftware.smack.XMPPConnection
import org.jivesoftware.smack.packet.Message
import sniper.app.Data
import sniper.app.Styles
import tornadofx.*

class MainView : View("Auction Sniper") {

    private val data: Data by inject()
    private val status = SimpleStringProperty()
    private lateinit var notToBeGCd: Chat;

    override val root = hbox {
        label {
            id = "main-label"
            addClass(Styles.heading)
            bind(status)
        }
    }

    init {
        status.value = "Joining"
        joinAuction(connection(data.hostname, data.username, data.password), data.itemId)
    }

    private fun joinAuction(connection: XMPPConnection, itemId: String) {
        val chat = connection.chatManager.createChat(
                auctionId(itemId, connection),
                MessageListener { _: Chat?, message: Message? ->
                    Platform.runLater {
                        status.value = "Lost"
                    }
                })
        this.notToBeGCd = chat
        chat.sendMessage(Message())
    }

    private fun connection(hostname: String, username: String, password: String): XMPPConnection {
        val connection = XMPPConnection(hostname)
        connection.connect()
        connection.login(username, password)
        return connection
    }

    private fun auctionId(itemId: String, connection: XMPPConnection): String {
        return String.format(AUCTION_ID_FORMAT, itemId, connection.serviceName)
    }

    companion object {
        const val AUCTION_RESOURCE: String = "Auction"
        const val ITEM_ID_AS_LOGIN = "auction-%s"
        const val AUCTION_ID_FORMAT: String = "$ITEM_ID_AS_LOGIN@%s/$AUCTION_RESOURCE"
    }
}

