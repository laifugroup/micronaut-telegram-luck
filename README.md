## Micronaut 4.7.0 Documentation

- [User Guide](https://docs.micronaut.io/4.7.0/guide/index.html)
- [API Reference](https://docs.micronaut.io/4.7.0/api/index.html)
- [Configuration Reference](https://docs.micronaut.io/4.7.0/guide/configurationreference.html)
- [Micronaut Guides](https://guides.micronaut.io/index.html)
---

# Telegram ChatBot

Follow the instructions in the [Micronaut ChatBot Documentation](https://micronaut-projects.github.io/micronaut-chatbots/latest/guide/) to create a Telegram ChatBot.

Once you have a username and HTTP auth key for your new bot, edit the application config in this project to set the bot username and make up a WEBHOOK_TOKEN so you can ensure it's Telegram that's calling your bot.

This project has a dependency on `micronaut-chatbots-telegram-http` which has added a controller to your application with the path `/telegram`.

When registering your bot with Telegram, you will need to provide the HTTPS URL of your application including this path.
If you are running your application locally, you can use a tool like [ngrok](https://ngrok.com/) to expose your application to the internet.

You can then set up the Telegram webhook by running the following command:

```bash
curl -X POST 'https://api.telegram.org/bot${HTTP_AUTH_KEY}/setWebhook?url=${YOUR_HTTP_TRIGGER_URL}&secret_token=${YOUR_SECRET_TOKEN}'
```

Where HTTP_AUTH_KEY is the key given to you by the BotFather, YOUR_HTTP_TRIGGER_URL is the URL of your HTTP function and YOUR_SECRET_TOKEN is the value you chose for the WEBHOOK_TOKEN in the configuration.


- [Micronaut Gradle Plugin documentation](https://micronaut-projects.github.io/micronaut-gradle-plugin/latest/)
- [GraalVM Gradle Plugin documentation](https://graalvm.github.io/native-build-tools/latest/gradle-plugin.html)
- [Shadow Gradle Plugin](https://plugins.gradle.org/plugin/com.github.johnrengelman.shadow)
## Feature chatbots-telegram-http documentation

- [Micronaut Telegram ChatBot as a controller documentation](https://micronaut-projects.github.io/micronaut-chatbots/latest/guide/)


## Feature micronaut-aot documentation

- [Micronaut AOT documentation](https://micronaut-projects.github.io/micronaut-aot/latest/guide/)


## Feature jdbc-hikari documentation

- [Micronaut Hikari JDBC Connection Pool documentation](https://micronaut-projects.github.io/micronaut-sql/latest/guide/index.html#jdbc)


## Feature openapi documentation

- [Micronaut OpenAPI Support documentation](https://micronaut-projects.github.io/micronaut-openapi/latest/guide/index.html)

- [https://www.openapis.org](https://www.openapis.org)


## Feature cache-caffeine documentation

- [Micronaut Caffeine Cache documentation](https://micronaut-projects.github.io/micronaut-cache/latest/guide/index.html)

- [https://github.com/ben-manes/caffeine](https://github.com/ben-manes/caffeine)


## Feature hibernate-validator documentation

- [Micronaut Hibernate Validator documentation](https://micronaut-projects.github.io/micronaut-hibernate-validator/latest/guide/index.html)


## Feature retry documentation

- [Micronaut Retry documentation](https://docs.micronaut.io/latest/guide/#retry)


## Feature json-schema-validation documentation

- [Micronaut JSON Schema Validation documentation](https://micronaut-projects.github.io/micronaut-json-schema/latest/guide/index.html#validation)


## Feature management documentation

- [Micronaut Management documentation](https://docs.micronaut.io/latest/guide/index.html#management)


## Feature micronaut-aop documentation

- [Micronaut Aspect-Oriented Programming (AOP) documentation](https://docs.micronaut.io/latest/guide/index.html#aop)


## Feature security-jwt documentation

- [Micronaut Security JWT documentation](https://micronaut-projects.github.io/micronaut-security/latest/guide/index.html)


## Feature json-schema documentation

- [Micronaut JSON Schema documentation](https://micronaut-projects.github.io/micronaut-json-schema/latest/guide/)

- [https://json-schema.org/learn/getting-started-step-by-step](https://json-schema.org/learn/getting-started-step-by-step)


## Feature test-resources documentation

- [Micronaut Test Resources documentation](https://micronaut-projects.github.io/micronaut-test-resources/latest/guide/)


## Feature swagger-ui documentation

- [Micronaut Swagger UI documentation](https://micronaut-projects.github.io/micronaut-openapi/latest/guide/index.html)

- [https://swagger.io/tools/swagger-ui/](https://swagger.io/tools/swagger-ui/)


## Feature kapt documentation

- [Micronaut Kotlin Annotation Processing (KAPT) documentation](https://docs.micronaut.io/snapshot/guide/#kapt)

- [https://kotlinlang.org/docs/kapt.html](https://kotlinlang.org/docs/kapt.html)


## Feature views-thymeleaf documentation

- [Micronaut Thymeleaf Views documentation](https://micronaut-projects.github.io/micronaut-views/latest/guide/index.html#thymeleaf)

- [https://www.thymeleaf.org/](https://www.thymeleaf.org/)


## Feature flyway documentation

- [Micronaut Flyway Database Migration documentation](https://micronaut-projects.github.io/micronaut-flyway/latest/guide/index.html)

- [https://flywaydb.org/](https://flywaydb.org/)


## Feature annotation-api documentation

- [https://jakarta.ee/specifications/annotations/](https://jakarta.ee/specifications/annotations/)


## Feature multi-tenancy documentation

- [Micronaut Multitenancy documentation](https://docs.micronaut.io/latest/guide/index.html#multitenancy)


## Feature email-javamail documentation

- [Micronaut Javamail Email documentation](https://micronaut-projects.github.io/micronaut-email/latest/guide/index.html#javamail)

- [https://jakartaee.github.io/mail-api/](https://jakartaee.github.io/mail-api/)


