package com.homefirst.kyc.netwokring

import com.fasterxml.jackson.databind.ObjectMapper
import com.homefirst.kyc.security.AppProperty
import com.homefirst.kyc.utils.CONTENT_TYPE
import com.homefirst.kyc.utils.CONTENT_TYPE_FORM_URLENCODED
import com.homefirst.kyc.utils.Errors
import com.homefirst.kyc.utils.MimeMap
import homefirst.utilities.utils.LocalHTTPResponse
import homefirst.utilities.utils.LoggerUtils
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.io.File
import java.io.FileOutputStream
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit

@Component
class CommonNetworkingClient(
    @Autowired val objectMapper: ObjectMapper,
    @Autowired val appProperty: AppProperty
) {

    private fun log(value: String) = LoggerUtils.log("v1/CommonNetworkingClient.$value")

    private fun printLog(value: String) = LoggerUtils.printLog("v1/CommonNetworkingClient.$value")

    companion object {
        private const val TIMEOUT = 60
        private lateinit var client: OkHttpClient
    }

    enum class HttpMethods {
        GET, POST, PATCH
    }

    init {
        client = OkHttpClient.Builder()
            .connectTimeout(TIMEOUT.toLong(), TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT.toLong(), TimeUnit.SECONDS)
            .readTimeout(TIMEOUT.toLong(), TimeUnit.SECONDS)
            .build()
    }

    inner class NewRequest {

        private lateinit var request: Request
        private var requestBuilder = Request.Builder()

        fun getCall(url: String): NewRequest {

            requestBuilder.method(HttpMethods.GET.name, null)
            requestBuilder.url(url)

            return this

        }

        fun postCall(url: String, requestJson: JSONObject): NewRequest {

            val mediaType = "application/json".toMediaTypeOrNull()
            val body = requestJson.toString().toRequestBody(mediaType)
            requestBuilder.method(HttpMethods.POST.name, body)
            requestBuilder.url(url)

            return this

        }

        fun patchCall(url: String, requestJson: JSONObject): NewRequest {

            val mediaType = "application/json".toMediaTypeOrNull()
            val body = requestJson.toString().toRequestBody(mediaType)
            requestBuilder.method(HttpMethods.PATCH.name, body)
            requestBuilder.url(url)

            return this

        }

        fun urlEncodedCall(
            url: String,
            params: HashMap<String, String>
        ): NewRequest {
            val bodyString = params
                .map { (key, value) ->
                    "${URLEncoder.encode(key, StandardCharsets.UTF_8)}=${
                        URLEncoder.encode(
                            value,
                            StandardCharsets.UTF_8
                        )
                    }"
                }
                .joinToString("&")

            val mediaType = CONTENT_TYPE_FORM_URLENCODED.toMediaTypeOrNull()
            val body = bodyString.toRequestBody(mediaType)
            requestBuilder.method(HttpMethods.POST.name, body)
            requestBuilder.url(url)
            requestBuilder.addHeader(CONTENT_TYPE, CONTENT_TYPE_FORM_URLENCODED)

            return this

        }

        fun postFormDataCall(url: String, requestBody: RequestBody): NewRequest {
            requestBuilder.url(url)
            requestBuilder.addHeader("Content-Type", "multipart/form-data")
            requestBuilder.post(requestBody)
            return this
        }

        fun addHeader(key: String, value: String): NewRequest {
            requestBuilder.addHeader(key, value)
            return this
        }

        fun send(): LocalHTTPResponse {

            request = requestBuilder.build()


            printLog("send - Request url: ${request.url}")
            printLog("send - Request headers: ${objectMapper.writeValueAsString(request.headers)}")
            printLog("send - Request body: ${request.body}")

            val localHTTPResponse = LocalHTTPResponse()

            val response = client.newCall(request).execute()
            val responseString = response.body!!.string()
            val responseCode = response.code
            response.body!!.close()
            response.close()

            printLog("send - Response code: $responseCode")
            printLog("send - Response body: $responseString")

            localHTTPResponse.statusCode = responseCode
            localHTTPResponse.stringEntity = responseString

            when (responseCode) {
                200 -> {
                    localHTTPResponse.isSuccess = true
                }

                401 -> {
                    localHTTPResponse.isSuccess = false
                    localHTTPResponse.errorMessage = Errors.UNAUTHORIZED_ACCESS.value
                }

                else -> {
                    localHTTPResponse.isSuccess = false
                    localHTTPResponse.errorMessage = responseString
                }
            }

            return localHTTPResponse

        }

        fun sendDoc(name: String?): LocalHTTPResponse {

            request = requestBuilder.build()

            printLog("sendDoc - Request url: ${request.url}")
            printLog("sendDoc - Request headers: ${objectMapper.writeValueAsString(request.headers)}")
            printLog("sendDoc - Request body: ${request.body}")

            val localHTTPResponse = LocalHTTPResponse()

            val response = client.newCall(request).execute()
            var responseBody: ResponseBody? = response.body

            val contentType = response.header("Content-Type")

            if (contentType == MimeMap.PDF.mime) {
                responseBody?.byteStream()?.use { inputStream ->
                    FileOutputStream(File("${appProperty.filePath}$name")).use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                    log("sendDoc - PDF file saved successfully")
                }
                localHTTPResponse.stringEntity = "Document saved"
            } else {
                localHTTPResponse.stringEntity = responseBody!!.string()
            }

            val responseCode = response.code
            localHTTPResponse.statusCode = responseCode
            response.close()

            printLog("sendDoc - Response code: $responseCode")
            printLog("sendDoc - Response body: $responseBody")

            when (responseCode) {
                200 -> {
                    localHTTPResponse.isSuccess = true
                }

                401 -> {
                    localHTTPResponse.isSuccess = false
                    localHTTPResponse.errorMessage = Errors.UNAUTHORIZED_ACCESS.value
                }

                else -> {
                    localHTTPResponse.isSuccess = false
                    localHTTPResponse.errorMessage = responseBody.toString()
                }
            }

            return localHTTPResponse

        }

        fun send201Success(): LocalHTTPResponse {

            request = requestBuilder.build()

            printLog("send - Request url: ${request.url}")
            printLog("send - Request headers: ${objectMapper.writeValueAsString(request.headers)}")
            printLog("send - Request body: ${request.body}")

            val localHTTPResponse = LocalHTTPResponse()

            val response = client.newCall(request).execute()
            val responseString = response.body!!.string()
            val responseCode = response.code
            response.body!!.close()
            response.close()

            printLog("send - Response code: $responseCode")
            printLog("send - Response body: $responseString")

            localHTTPResponse.statusCode = responseCode
            localHTTPResponse.stringEntity = responseString

            when (responseCode) {
                201 -> {
                    localHTTPResponse.isSuccess = true
                }

                401 -> {
                    localHTTPResponse.isSuccess = false
                    localHTTPResponse.errorMessage = Errors.UNAUTHORIZED_ACCESS.value
                }

                else -> {
                    localHTTPResponse.isSuccess = false
                    localHTTPResponse.errorMessage = responseString
                }
            }

            return localHTTPResponse

        }

    }



}