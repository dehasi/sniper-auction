package test.app

import org.apache.commons.io.FileUtils
import org.apache.commons.io.FileUtils.readFileToString
import org.assertj.core.api.Assertions.assertThat
import java.io.File
import java.util.logging.LogManager
import kotlin.text.Charsets.UTF_8

class AuctionLogDriver {
    companion object {
        const val LOG_FILE_NAME = "auction-sniper.log"
    }

    private val logFile = File(LOG_FILE_NAME)

    fun clearLog() {
        logFile.delete()
        LogManager.getLogManager().reset()
    }

    fun hasEntry(predicate: (String) -> Boolean) {
        assertThat(readFileToString(logFile, UTF_8)).matches(predicate)
    }

}
