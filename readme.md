# Login with twitter Web

# Deploy

Google App Engine Standard Environment (Java8)

https://twitter-dot-tfandkusu.appspot.com/

# Libraries

- [Ktor](https://ktor.io/)
- [Google App Engine Gradle plugin](https://github.com/GoogleCloudPlatform/app-gradle-plugin)
- [Twitter4j](https://github.com/Twitter4J/Twitter4J)
- [logback-classic](https://github.com/qos-ch/logback)

# How to deploy

Move to [template-app-engine.xml](https://github.com/tfandkusu/login_with_twitter_web/blob/master/src/main/webapp/WEB-INF/template-appengine-web.xml) to `app-engine.xml` and write your settings.

Set `GCP_PROJECT_ID` environment variable as GCP project id to deploy.

```sh
./gradlew appengineDeploy
```
