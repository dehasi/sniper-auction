package sniper.app

import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import java.util.logging.LogManager
import java.util.logging.Logger

@TestInstance(PER_CLASS)
internal class LoggingXMPPFailureReporterTest {

    private val logger: Logger = mockk(relaxUnitFun = true)
    private val reporter: LoggingXMPPFailureReporter = LoggingXMPPFailureReporter(logger)

    @AfterAll fun resetLogging() {
        LogManager.getLogManager().reset()
    }

    @Test fun cannotTranslateMessage() {
        reporter.cannotTranslateMessage("auction id", "bad message", Exception("bad"))

        verify {
            logger.severe(""
                    + "<auction id>"
                    + " Could not translate message \"bad message\""
                    + " because java.lang.Exception: bad")
        }
    }
}
