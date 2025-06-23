package com.homefirst.kyc.routes

import com.homefirst.kyc.dto.EPAuthRequest
import com.homefirst.kyc.dto.PanAuthRequest
import com.homefirst.kyc.service.KycService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.kycRoutes(
    kycService: KycService
) {
    route("/kyc") {

        get("/hello") {
            call.respond ("<html> " + "<title>" + "HomeFirst Microservice" + "</title>" + "<body><h1>"
                    + "Successfully deployed KYC Service." + "</h1></body>" + "</html> ")
        }

        get("/validate.aadhaar/{aadhaarNumber}") {
            val aadhaarNumber = call.parameters["aadhaarNumber"]
                ?: return@get call.respond(HttpStatusCode.BadRequest, "aadhaarNumber missing")

            try {
                val epAuthRequest = EPAuthRequest(call)
                val responseEntity = kycService.validateAadhaar(epAuthRequest, aadhaarNumber)

                val responseStatus = HttpStatusCode.fromValue(responseEntity.statusCode.value())
                val responseBody = responseEntity.body ?: ""

                call.respond(status = responseStatus, message = responseBody)
            } catch (e: Exception) {
                e.printStackTrace()
                call.respond(HttpStatusCode.InternalServerError, "Aadhaar validation failed: ${e.message}")
            }
        }

        get("/validate.passport") {
            val fileNumber = call.request.queryParameters["fileNumber"]
            val dob = call.request.queryParameters["dob"]

            try {
                val epAuthRequest = EPAuthRequest(call)
                val responseEntity = kycService.validatePassport(epAuthRequest, fileNumber!!,dob!!)

                val responseStatus = responseEntity?.statusCode?.let { it1 -> HttpStatusCode.fromValue(it1.value()) }
                val responseBody = responseEntity?.body ?: ""

                call.respondText("Validating Passport: $fileNumber, $dob")

                if (responseStatus != null) {
                    call.respond(status = responseStatus, message = responseBody)
                }
            }catch (e : Exception){
                e.printStackTrace()
                call.respond( "Aadhaar validation failed: ${e.message}")
            }
        }

        post("/authenticate.pan") {
            val request = call.receive<PanAuthRequest>()
            println("panAUth-->>${request}")

            try {
                println("panAUth-->>${request}")

                val epAuthRequest = EPAuthRequest(call)

                val result = kycService.authenticatePan(epAuthRequest, request.pan!!)

                val responseStatus = result?.statusCode?.let { it1 -> HttpStatusCode.fromValue(it1.value()) }
                val responseBody = result?.body ?: ""

                if (responseStatus != null) {
                    call.respond(responseStatus,responseBody)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                call.respond("Exception occurred while authenticating pan: ${e.message}")
            }
        }

    }
}
