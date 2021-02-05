package com.github.kotlintelegrambot.network.apiclient

import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.testutils.decode
import junit.framework.TestCase.assertEquals
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.Test

class UnpinAllChatMessagesIT : ApiClientIT() {

    @Test
    fun `correct request on unpinAllChatMessages with chat id`() {
        givenASuccessfulUnpinAllChatMessagesResponse()

        sut.unpinAllChatMessages(ChatId.fromId(ANY_CHAT_ID))

        val request = mockWebServer.takeRequest()
        val requestMethodName = request.path?.split("/")?.lastOrNull()
        val requestBody = request.body.readUtf8().decode()
        assertEquals("unpinAllChatMessages", requestMethodName)
        assertEquals("chat_id=$ANY_CHAT_ID", requestBody)
    }

    @Test
    fun `correct request on unpinAllChatMessages with channel username`() {
        givenASuccessfulUnpinAllChatMessagesResponse()

        sut.unpinAllChatMessages(ChatId.fromChannelUsername(ANY_CHANNEL_USERNAME))

        val request = mockWebServer.takeRequest()
        val requestMethodName = request.path?.split("/")?.lastOrNull()
        val requestBody = request.body.readUtf8().decode()
        assertEquals("unpinAllChatMessages", requestMethodName)
        assertEquals("chat_id=$ANY_CHANNEL_USERNAME", requestBody)
    }

    @Test
    fun `successful unpinAllChatMessages response is returned correctly`() {
        givenASuccessfulUnpinAllChatMessagesResponse()

        val unpinChatMessageResult = sut.unpinAllChatMessages(
            ChatId.fromChannelUsername(ANY_CHANNEL_USERNAME)
        ).getOrNull()

        assertEquals(false, unpinChatMessageResult)
    }

    private fun givenASuccessfulUnpinAllChatMessagesResponse() {
        val unpinAllChatMessagesResponse = """
            {
                "ok": true,
                "result": false
            }
        """.trimIndent()
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(unpinAllChatMessagesResponse)
        )
    }

    private companion object {
        const val ANY_CHAT_ID = 1412412L
        const val ANY_CHANNEL_USERNAME = "@kotlin"
    }
}
