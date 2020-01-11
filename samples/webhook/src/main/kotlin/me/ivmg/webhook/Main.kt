package me.ivmg.webhook

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receiveText
import io.ktor.response.respond
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import me.ivmg.telegram.bot
import me.ivmg.telegram.dispatch
import me.ivmg.telegram.dispatcher.command
import me.ivmg.telegram.entities.TelegramFile

object MyBotConfig {
    const val API_TOKEN = "ANY_TOKEN"
}

fun main() {
    val botInstance = bot {
        token = MyBotConfig.API_TOKEN
        dispatch {
            command("hello") { _, update, _ ->
                val chatId = update.message?.chat?.id ?: return@command
                bot.sendMessage(chatId, "Hey bruh!")
            }
        }
    }

    botInstance.deleteWebhook()
    botInstance.setWebhook(
        "https://c5e6b19e.ngrok.io/${MyBotConfig.API_TOKEN}",
        TelegramFile.ByFile(getFileFromResources("certificate.pem")),
        50,
        listOf("message", "edited_channel_post", "callback_query")
    )
    botInstance.startCheckingUpdates()
    val webhookInfo = botInstance.getWebhookInfo()
    print(webhookInfo)

    embeddedServer(Netty) {
        routing {
            post("/${MyBotConfig.API_TOKEN}") {
                val response = call.receiveText()
                botInstance.processUpdate(response)
                call.respond(HttpStatusCode.OK)
            }
        }
    }.start(wait = true)
}