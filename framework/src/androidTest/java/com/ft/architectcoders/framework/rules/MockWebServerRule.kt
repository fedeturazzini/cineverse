package com.ft.architectcoders.framework.rules

import okhttp3.mockwebserver.MockWebServer
import org.junit.rules.TestWatcher
import org.junit.runner.Description

class MockWebServerRule : TestWatcher() {
    val server = MockWebServer()

    override fun starting(description: Description?) {
        super.starting(description)
        server.start()
    }

    override fun finished(description: Description?) {
        super.finished(description)
        server.shutdown()
    }
}

