package com.homefirst.kyc.dto

import com.homefirst.kyc.utils.AUTHORIZATION
import com.homefirst.kyc.utils.ORG_ID
import com.homefirst.kyc.utils.SESSION_PASSCODE
import com.homefirst.kyc.utils.getClientCreds
import homefirst.utilities.utils.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import jakarta.servlet.http.HttpServletRequest

class EPAuthRequest() {

    var authorization = NA
    var clientId = NA
    var clientSecret = NA
    var orgId = NA
    var sessionPasscode = NA
    var ipAddress = NA
    var requestUri = NA

    constructor(request: HttpServletRequest): this() {

        request.run {

            authorization = getHeader(AUTHORIZATION)
            orgId = getHeader(ORG_ID)
            ipAddress = getIPAddress(this)
            requestUri = requestURI

            getHeader(SESSION_PASSCODE)?.let {
                sessionPasscode = it
            }

            getClientCreds(authorization).let {
                clientId = it.clientId
                clientSecret = it.clientSecret
            }

        }

    }

    constructor(call: ApplicationCall) : this() {
        val request = call.request

        authorization = request.headers[AUTHORIZATION] ?: NA
        orgId = request.headers[ORG_ID] ?: NA
        sessionPasscode = request.headers[SESSION_PASSCODE] ?: request.queryParameters[SESSION_PASSCODE] ?: NA
        ipAddress = request.origin.remoteHost
        requestUri = request.uri

        getClientCreds(authorization).let {
            clientId = it.clientId
            clientSecret = it.clientSecret
        }
    }


    fun isRequestValid(): Boolean {
        return authorization.isNotNullOrNA() && orgId.isNotNullOrNA()
    }

}

class BasicAuthCreds(
    var clientId: String = NA,
    var clientSecret: String = NA
)
