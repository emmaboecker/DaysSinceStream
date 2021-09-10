package net.stckoverflw.dss.config

object Config {

    val TWITTER_BEARER_TOKEN: String by getEnv()
    val TWITTER_API_KEY: String by getEnv()
    val TWITTER_API_SECRET: String by getEnv()
    val TWITTER_ACCESS_TOKEN: String by getEnv()
    val TWITTER_ACCESS_SECRET: String by getEnv()

    val TWITCH_CLIENT_ID: String by getEnv()
    val TWITCH_ACCESS_TOKEN: String by getEnv()

    val TWITCH_CHANNEL_ID: String by getEnv()
}