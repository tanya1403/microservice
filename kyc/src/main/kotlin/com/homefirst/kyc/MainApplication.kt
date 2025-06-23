package com.homefirst.kyc

import com.homefirst.kyc.routes.kycRoutes
import com.homefirst.kyc.service.KycService
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

lateinit var springContext: ApplicationContext

@SpringBootApplication
@EntityScan(basePackages = ["com.homefirst.kyc.model","com.homefirst.utilities.model"])
@EnableJpaRepositories(basePackages = ["com.homefirst.kyc.repository","com.homefirst.utilities.repository"])
@ComponentScan("com.homefirst.kyc","com.homefirst.utilities")
class MainApplication
fun main() {
    springContext = org.springframework.boot.runApplication<MainApplication>()

    embeddedServer(Netty, port = 8080, module = Application::kycModule).start(wait = true)
}

fun Application.kycModule() {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respond(HttpStatusCode.InternalServerError, "Internal server error: ${cause.message}")
        }
    }

    val kycService = springContext.getBean(KycService::class.java)

    routing {
        route("/microservice") {
            kycRoutes(kycService)
        }
    }
}
