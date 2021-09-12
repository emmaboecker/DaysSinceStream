package net.stckoverflw.dss

import com.google.gson.JsonParser
import com.soywiz.klock.jvm.toDateTime
import dev.inmo.krontab.doWhile
import io.github.redouane59.twitter.TwitterClient
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import kotlinx.coroutines.*
import net.stckoverflw.dss.config.Config
import net.stckoverflw.dss.extension.get
import net.stckoverflw.dss.twitter.credentials
import org.apache.http.HttpHeaders
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.Future
import kotlin.coroutines.resumeWithException

object TwitterBot {

    lateinit var twitter: TwitterClient

    lateinit var client: HttpClient

    @OptIn(DelicateCoroutinesApi::class)
    suspend operator fun invoke() {
        println("Starting Twitter Bot")

        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Berlin"))

        twitter = TwitterClient(credentials {
            apiKey = Config.TWITTER_API_KEY
            apiSecretKey = Config.TWITTER_API_SECRET
            accessToken = Config.TWITTER_ACCESS_TOKEN
            accessTokenSecret = Config.TWITTER_ACCESS_SECRET
        })

        println("Logged into Twitter")

        client = HttpClient(OkHttp) {
            install(JsonFeature) {
                serializer = KotlinxSerializer()
            }
        }

        println("Initialized HttpClient")

        GlobalScope.launch {
            doWhile("0 0 20 * * *") {
                postLastStreamDay(lastStream())
                true
            }
        }

        println("Launched Coroutine")

        twitter.startFilteredStream{}.await()
    }

    private fun postLastStreamDay(lastStreamDay: Int) {
        val currentDay = Calendar.getInstance().time.toDateTime().dayOfYear
        val difference = currentDay - lastStreamDay
        twitter.postTweet(formatMessage(difference))
    }

    private fun formatMessage(difference: Int): String {
        val builder = StringBuilder()
        builder.append(difference)
        builder.append(if (difference != 1) " days" else "st day")
        builder.append(" without a TommyInnit Stream ")
        builder.append(
            if (difference > 7)
                ":("
            else if (difference >= 3)
                ":/"
            else if (difference == 0)
                ":)"
            else
                ""
        )
        return builder.toString()
    }

    private suspend fun lastStream(): Int {
        val rawJsonResponse: String =
            client.get("https://api.twitch.tv/helix/videos?user_id=${Config.TWITCH_CHANNEL_ID}&first=1") {
                header(HttpHeaders.AUTHORIZATION, "Bearer ${Config.TWITCH_ACCESS_TOKEN}")
                header("Client-Id", Config.TWITCH_CLIENT_ID)
            }

        val json = JsonParser.parseString(rawJsonResponse)

        val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")

        return dateFormat.parse(json["data"][0]["created_at"].asString).toDateTime().dayOfYear
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun <T> Future<T>.await(): T = suspendCancellableCoroutine { cont ->
        ForkJoinPool.managedBlock(object : ForkJoinPool.ManagedBlocker {
            override fun block(): Boolean {
                try {
                    cont.resume(get()) { cont.cancel(it) }
                } catch (e: Throwable) {
                    cont.resumeWithException(e)
                    return false
                }

                return true
            }

            override fun isReleasable(): Boolean = isDone
        })
    }
}