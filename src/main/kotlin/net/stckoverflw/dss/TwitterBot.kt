package net.stckoverflw.dss


import com.google.gson.JsonParser
import dev.inmo.krontab.doWhile
import io.github.redouane59.twitter.TwitterClient
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.http.auth.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import net.stckoverflw.dss.config.Config
import net.stckoverflw.dss.twitter.credentials
import org.apache.http.HttpHeaders
import java.util.*

object TwitterBot {

    lateinit var twitter: TwitterClient

    lateinit var client: HttpClient

    @OptIn(DelicateCoroutinesApi::class)
    suspend operator fun invoke() {
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Berlin"))

        twitter = TwitterClient(credentials {
            apiKey = Config.TWITTER_API_KEY
            apiSecretKey = Config.TWITTER_API_SECRET
            accessToken = Config.TWITTER_ACCESS_TOKEN
            accessTokenSecret = Config.TWITTER_ACCESS_SECRET
        })

        client = HttpClient(OkHttp) {
            install(JsonFeature) {
                serializer = KotlinxSerializer()
            }
        }

        GlobalScope.launch {
            doWhile("0 0 10 * * *") {
                twitter.postTweet("")
                true
            }
        }
    }

    private suspend fun lastStream(): Instant {

        val rawOAuthJson: String =
            client.get("https://id.twitch.tv/oauth2/authorize?response_type=code&client_id=&redirect_uri=http://localhost&scope=viewing_activity_read") {

            }

        val rawJsonResponse: String =
            client.get("https://api.twitch.tv/helix/videos?user_id=${Config.TWITCH_CHANNEL_ID}&first=1") {
                header(HttpHeaders.AUTHORIZATION, "Bearer ${Config.TWITCH_BEARER_TOKEN}")
                header("Client-Id", Config.TWITCH_CLIENT_ID)
            }

        JsonParser.parseString(rawJsonResponse)

        TODO("Return last Stream Date")
    }
}