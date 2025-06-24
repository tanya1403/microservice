package com.homefirst.microservices

import com.homefirst.microservices.route.publicRoutes
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
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationContext

lateinit var springContext: ApplicationContext


@SpringBootApplication
class MicroservicesApplication

fun main() {
	springContext = runApplication<MicroservicesApplication>()

	embeddedServer(Netty, port = 8081, module = Application::PublicModule).start(wait = true)
}

fun Application.PublicModule() {
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

	routing {
		route("/microservice") {
			publicRoutes()
		}
	}
}