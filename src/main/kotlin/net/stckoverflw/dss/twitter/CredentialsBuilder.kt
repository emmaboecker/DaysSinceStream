package net.stckoverflw.dss.twitter

import io.github.redouane59.twitter.signature.TwitterCredentials

fun credentials(builder: TwitterCredentials.() -> Unit): TwitterCredentials {
    val credentials = TwitterCredentials()
    credentials.apply(builder)
    return credentials
}