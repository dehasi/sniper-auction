package sniper.eventhandling

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import sniper.app.UserRequestListener

internal class AnnouncerTest {

    @Test fun announce() {
        val receivedValue = mutableListOf<String>()
        val announcer = Announcer.`for`(UserRequestListener::class.java)

        announcer.addListener(object : UserRequestListener {
            override fun joinAuction(itemId: String) {
                receivedValue.add(itemId)
            }
        })

        announcer.announce().joinAuction("an item-id")

        assertThat(receivedValue).containsExactly("an item-id")
    }
}
