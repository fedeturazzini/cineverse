package com.ft.architectcoders.framework.rules

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer

fun MockWebServer.enqueueResponse(fileName: String, code: Int = 200) {
    val json = javaClass.classLoader!!
        .getResourceAsStream("api-response/$fileName")!!
        .bufferedReader()
        .use { it.readText() }

    enqueue(
        MockResponse()
            .setResponseCode(code)
            .setBody(json)
    )
}

