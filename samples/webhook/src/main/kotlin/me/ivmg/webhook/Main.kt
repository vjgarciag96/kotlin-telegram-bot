package me.ivmg.webhook

import io.ktor.application.call
import io.ktor.html.respondHtml
import io.ktor.http.HttpStatusCode
import io.ktor.request.receiveText
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.applicationEngineEnvironment
import io.ktor.server.engine.embeddedServer
import io.ktor.server.engine.sslConnector
import io.ktor.server.netty.Netty
import kotlinx.html.body
import kotlinx.html.head
import kotlinx.html.p
import kotlinx.html.title
import me.ivmg.telegram.bot
import me.ivmg.telegram.dispatch
import me.ivmg.telegram.dispatcher.command

object MyBotConfig {
    const val API_TOKEN = "382580035:AAGPliJG3RDqthBK6a6pqOpnjjVh58pme7I"
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
        url = "https://vjgarcia.pw/${MyBotConfig.API_TOKEN}",
        //certificate = TelegramFile.ByFile(getFileFromResources("cert.pem")),
        maxConnections = 50,
        allowedUpdates = listOf("message", "edited_channel_post", "callback_query")
    )
    botInstance.startCheckingUpdates()
    val webhookInfo = botInstance.getWebhookInfo()
    print(webhookInfo)

    val env = applicationEngineEnvironment {
        module {
            routing {
                post("/${MyBotConfig.API_TOKEN}") {
                    val response = call.receiveText()
                    botInstance.processUpdate(response)
                    call.respond(HttpStatusCode.OK)
                }
                get("/") {
                    call.respondHtml {
                        head {
                            title { +"Ktor: netty" }
                        }
                        body {
                            p {
                                +"Hello from Ktor Netty engine sample application"
                            }
                        }
                    }
                }
            }
        }
        sslConnector(
            keyStore = CertificateFactory.get(),
            keyAlias = "mykey",
            keyStorePassword = { "changeit".toCharArray() },
            privateKeyPassword = { "changeit".toCharArray() }) {
            port = 443
            keyStorePath = CertificateFactory.keyStoreFile().absoluteFile
            host = "0.0.0.0"
        }
    }
    embeddedServer(Netty, env).start(wait = true)
}