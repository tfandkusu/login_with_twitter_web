package com.tfandkusu.twitter.login

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.DefaultHeaders
import io.ktor.html.respondHtml
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.sessions.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.html.*
import twitter4j.TwitterFactory
import twitter4j.auth.RequestToken

fun Application.main() {
    install(DefaultHeaders)
    install(CallLogging)
    install(Sessions) {
        cookie<MySession>("MY_SESSION")
    }
    val twitterApiKey = System.getenv("TWITTER_API_KEY")
    val twitterApiSecretKey = System.getenv("TWITTER_API_SECRET_KEY")
    val twitterRedirectUrl = System.getenv("TWITTER_REDIRECT_URL")
    routing {
        get("/") {
            val twitterLogin = withContext(Dispatchers.IO) {
                val twitter = TwitterFactory().instance
                twitter.setOAuthConsumer(twitterApiKey, twitterApiSecretKey)
                val requestToken = twitter.getOAuthRequestToken(twitterRedirectUrl)
                TwitterLogin(requestToken.authorizationURL, requestToken.token, requestToken.tokenSecret)
            }
            call.sessions.set(MySession(twitterLogin.token, twitterLogin.tokenSecret))
            call.respondHtml {
                head {
                    title { +"Login with twitter" }
                }
                body {
                    p {
                        a(href = twitterLogin.authorizationURL) {
                            +"Login with twitter"
                        }
                    }
                }
            }
        }
        get("/redirect_to") {
            val twitter = TwitterFactory().instance
            twitter.setOAuthConsumer(twitterApiKey, twitterApiSecretKey)
            val session = call.sessions.get<MySession>()
            if (session == null) {
                call.respondHtml(HttpStatusCode.Forbidden) {
                    head {
                        title { +"Redirect page" }
                    }
                    body {
                        p {
                            +"Session error"
                        }
                    }
                }
            } else {
                val token = session.token
                val tokenSecret = session.tokenSecret
                // TODO パラメータチェックする
                val oauthVerifier = call.request.queryParameters["oauth_verifier"]
                val accessToken = withContext(Dispatchers.IO) {
                    twitter.getOAuthAccessToken(RequestToken(token, tokenSecret), oauthVerifier)
                }
                call.respondHtml {
                    head {
                        title { +"Redirect page" }
                    }
                    body {
                        p {
                            br {
                                +"userId = %s".format(accessToken.userId)
                            }
                        }
                    }
                }
            }
        }
    }
}
