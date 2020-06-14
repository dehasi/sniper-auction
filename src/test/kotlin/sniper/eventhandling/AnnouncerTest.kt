package sniper.eventhandling

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import sniper.app.UserRequestListener

internal class AnnouncerTest {

    @Test fun `announce calls all listeners`() {
        val receivedValue = mutableListOf<String>()
        val announcer = Announcer.to(UserRequestListener::class.java)

        announcer.addListener(object : UserRequestListener {
            override fun joinAuction(itemId: String) {
                receivedValue.add("$itemId-1")
            }
        })

        announcer.addListener(object : UserRequestListener {
            override fun joinAuction(itemId: String) {
                receivedValue.add("$itemId-2")
            }
        })

        announcer.announce().joinAuction("item-id")

        assertThat(receivedValue).containsExactly("item-id-1", "item-id-2")
    }
}
