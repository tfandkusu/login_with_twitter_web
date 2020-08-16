package com.tfandkusu.twitter.login

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.DefaultHeaders
import io.ktor.html.respondHtml
import io.ktor.routing.get
import io.ktor.routing.routing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.html.*
import twitter4j.TwitterFactory

fun Application.main() {
    install(DefaultHeaders)
    install(CallLogging)
    val twitterApiKey = System.getenv("TWITTER_API_KEY")
    val twitterApiSecretKey = System.getenv("TWITTER_API_SECRET_KEY")
    val twitterRedirectUrl = System.getenv("TWITTER_REDIRECT_URL")

    routing {
        get("/") {
            val loginUrl = withContext(Dispatchers.IO) {
                val twitter = TwitterFactory().instance
                twitter.setOAuthConsumer(twitterApiKey, twitterApiSecretKey)
                val requestToken = twitter.getOAuthRequestToken(twitterRedirectUrl)
                requestToken.authorizationURL
            }
            call.respondHtml {
                head {
                    title { +"Login with twitter" }
                }
                body {
                    p {
                        a(href = loginUrl) {
                            +"Login with twitter"
                        }
                    }
                }
            }
        }
    }
}
