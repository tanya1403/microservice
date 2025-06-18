package com.homefirst.kyc.client


import com.homefirst.kyc.manager.CredsManager
import com.homefirst.kyc.manager.EnCredType
import com.homefirst.kyc.manager.EnPartnerName
import com.homefirst.kyc.model.Creds
import com.homefirst.kyc.netwokring.CommonNetworkingClient
import com.homefirst.kyc.utils.AUTHORIZATION
import com.homefirst.kyc.utils.CONTENT_TYPE
import com.homefirst.kyc.utils.CryptoUtils
import homefirst.utilities.utils.*
import org.apache.commons.lang3.RandomStringUtils
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import java.util.*

@Configuration
class DigitapClient(
    @Autowired val cryptoUtils: CryptoUtils,
    @Autowired val credentialManager: CredsManager,
    @Autowired val commonNetworkingClient: CommonNetworkingClient,

    ) {

    private var _digitapCred: Creds? = null

    private fun log(value: String) = LoggerUtils.log("DigitapClient.$value")

    @Throws(Exception::class)
    private fun digitapCred(): Creds? {
        if (null == _digitapCred) {
            _digitapCred = credentialManager.fetchCredentials(
                EnPartnerName.DIGITAP,
                if (cryptoUtils.appProperty.isProduction()) EnCredType.UAT else EnCredType.UAT
            )
        }
        return _digitapCred
    }

    enum class EnEndPointUrl(val value: String) {
        AADHAAR_VALIDATION("/validation/kyc/v1/aadhaar"),
        PASSPORT_VALIDATION("/validation/kyc/v1/passport");


        fun getFullApiUrl(baseUrl: String): String {
            return baseUrl + value
        }

    }


    @Throws(Exception::class)
    fun aadhaarValidation(aadhaarNumber: String): LocalResponse {

        val requestBody = JSONObject().apply {
            put("client_ref_num", "av${RandomStringUtils.randomNumeric(8).uppercase()}")
            put("aadhaar", aadhaarNumber)
        }

        val aadhaarValUrl = EnEndPointUrl.AADHAAR_VALIDATION.getFullApiUrl("${digitapCred()?.apiUrl}")

//        val username = digitapCred()?.username ?: throw IllegalStateException("Username missing")
//        val password = digitapCred()?.password ?: throw IllegalStateException("Password missing")
//        val credentials = "$username:$password"
//        val encoded = Base64.getEncoder().encodeToString(credentials.toByteArray())
//
//        println("encoded-->>${encoded}")
//
//        val decode = cryptoUtils.decodeBase64(encoded)
//
//        println("decode-->>${decode}")

        val credentials = "75080260:rJPWNAWUcizufjYl9MVIi9uWsCnhGm7j"
        val encoded = Base64.getEncoder().encodeToString(credentials.toByteArray())
        println("Encoded: $encoded")


        val decode = cryptoUtils.decodeBase64(encoded)
//
        println("decode-->>${decode}")

        val localHTTPResponse = commonNetworkingClient
            .NewRequest()
            .postCall(aadhaarValUrl, requestBody)
            .addHeader(CONTENT_TYPE, CONTENT_TYPE_APPLICATION_JSON)
//            .addHeader(AUTHORIZATION, "Basic $encoded")
            .addHeader(
                AUTHORIZATION, cryptoUtils.generateBasicAuth(
                    "75080260", //digitapCred()?.username!!,
                    "rJPWNAWUcizufjYl9MVIi9uWsCnhGm7j"//digitapCred()?.password!!
                )
            )
            .send()



        log("aadhaarValidation - digitap response: ${localHTTPResponse.stringEntity}")

        localHTTPResponse.stringEntity = "{\n" +
                "  \"httpStatus\": 200,\n" +
                "  \"isValid\": true,\n" +
                "  \"request_id\": \"006No00000LsehB\",\n" +
                "  \"result\": {\n" +
                "    \"aadhaar_result\": \"Aadhaar Number XXXXXXXX3866 Exists!\",\n" +
                "    \"aadhaar_age_band\": \"40-50\",\n" +
                "    \"aadhaar_gender\": \"MALE\",\n" +
                "    \"aadhaar_phone\": null,\n" +
                "    \"aadhaar_state\": \"Gujarat\"\n" +
                "  },\n" +
                "  \"status-code\": 101\n" +
                "}"
        val responseJson = JSONObject(localHTTPResponse.stringEntity)

        val localResponse = LocalResponse().apply {
            message = localHTTPResponse.stringEntity
            isSuccess = responseJson.optInt("httpStatus", -1) == 200
        }

        return localResponse

    }

    @Throws(Exception::class)
    fun passportValidation(fileNumber: String, dob: String): LocalResponse {

        val requestBody = JSONObject().apply {
            put("client_ref_num", "pass${RandomStringUtils.randomNumeric(8).uppercase()}")
            put("file_number", "fileNumber")
            put("dob", dob)
        }

        val passportValUrl = EnEndPointUrl.PASSPORT_VALIDATION.getFullApiUrl("${digitapCred()?.apiUrl}")

        val localHTTPResponse = commonNetworkingClient
            .NewRequest()
            .postCall(passportValUrl, requestBody)
            .addHeader(CONTENT_TYPE, CONTENT_TYPE_APPLICATION_JSON)
            .addHeader(
                AUTHORIZATION, cryptoUtils.generateBasicAuth(
                    digitapCred()?.username!!,
                    digitapCred()?.password!!
                )
            )
            .send()

        log("passportValidation - digitap response: ${localHTTPResponse.stringEntity}")

        val responseJson = JSONObject(localHTTPResponse.stringEntity)

        val localResponse = LocalResponse().apply {
            message = localHTTPResponse.stringEntity
            isSuccess = responseJson.optInt("http_response_code", -1) == 200
        }

        return localResponse

    }
}