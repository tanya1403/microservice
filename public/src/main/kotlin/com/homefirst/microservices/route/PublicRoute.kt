package com.homefirst.microservices.route

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.publicRoutes(
) {

    route("/public") {

        get("/hello") {
            call.respond ("<html> " + "<title>" + "HomeFirst Microservice" + "</title>" + "<body><h1>"
                    + "Successfully deployed Public Service." + "</h1></body>" + "</html> ")

        }
    }
}