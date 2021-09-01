package net.stckoverflw.dss

import com.google.gson.JsonParser
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.http.auth.*
import net.stckoverflw.dss.config.Config
import org.apache.http.HttpHeaders

suspend fun main() {
    val client = HttpClient(OkHttp) {
        install(JsonFeature) {
            serializer = KotlinxSerializer()
        }
    }

    val rawJsonResponse: String =
        client.get("https://api.twitch.tv/helix/videos?user_id=${Config.TWITCH_CHANNEL_ID}&first=1") {
            header(HttpHeaders.AUTHORIZATION, "Bearer ${Config.TWITCH_BEARER_TOKEN}")
            header("Client-Id", Config.TWITCH_CLIENT_ID)
        }

    val json = JsonParser.parseString(rawJsonResponse)

    println(json)
}