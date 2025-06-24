package com.homefirst.kyc.client

import com.homefirst.kyc.manager.CredsManager
import com.homefirst.kyc.manager.EnCredType
import com.homefirst.kyc.manager.EnPartnerName
import com.homefirst.kyc.model.Creds
import com.homefirst.kyc.netwokring.CommonNetworkingClient
import com.homefirst.kyc.security.AppProperty
import com.homefirst.kyc.utils.CryptoUtils
import com.homefirst.kyc.utils.LocalResponse
import com.homefirst.kyc.utils.LoggerUtils
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration

@Configuration
class KarzaClient(
    @Autowired private val appProperty: AppProperty,
    @Autowired private val commonNetworkingClient: CommonNetworkingClient,
    @Autowired private val cryptoUtils: CryptoUtils,
    @Autowired private val credsManager: CredsManager,

    ){

    private var credential: Creds? = credsManager.fetchCredentials(
        EnPartnerName.KARZA,
        if (cryptoUtils.appProperty.isProduction()) EnCredType.UAT else EnCredType.UAT
    )

    enum class EnAPIDetail(val endPoint: String, val type: String?) {
        KYC_OCR("/v3/ocr/kyc","KYC_OCR"),
        ITR_OCR("/v2/ocr/itrv","ITR_OCR"),
        AUTH_PAN("/v2/pan","AUTH_PAN"),
        AUTH_ITR("/v2/itr","AUTH_ITR"),
        AUTH_ELECTRICITY("/v2/elec","AUTH_ELECTRICITY"),
        AUTH_TELEPHONE("/v2/tele","AUTH_TELEPHONE"),
        AUTH_LPG("/v2/lpg","AUTH_LPG"),
        AUTH_GSTIN("/v2/gstdetailed","AUTH_GSTIN"),
        AUTH_VEHICLE_RC("/v2/rc","AUTH_VEHICLE_RC"),
        MOBILE_OTP("/v2/mobile/otp","MOBILE_OTP"),
        MOBILE_STATUS("/v2/mobile/status","MOBILE_STATUS"),
        MOBILE_DETAILS("/v2/mobile/details","MOBILE_DETAILS"),
        EPF("/v2/epf-get-otp","EPF"),
        EPF_GET_PASSBOOK("/v2/epf-get-passbook","EPF_GET_PASSBOOK"),
        UAN_LOOKUP("/v2/uan-lookup","UAN_LOOKUP");

        fun getFullApiUrl(baseUrl: String): String {
            return baseUrl + endPoint
        }

    }


     fun clientAPICall(enAPIDetail: EnAPIDetail, docType: String?, requestBody: JSONObject): LocalResponse {

        credential ?: run {
            LoggerUtils.log("No credential found for partner: ${EnPartnerName.KARZA}")
            return LocalResponse().apply {
                isSuccess = false
                message = "No credential found for partner: ${EnPartnerName.KARZA}"
            }
        }
        val localResponse = LocalResponse()

        if (cryptoUtils.appProperty.isProduction()) {

            val localHTTPResponse = commonNetworkingClient.NewRequest()
                .postCall(enAPIDetail.getFullApiUrl(credential!!.apiUrl!!), requestBody)
                .addHeader("content-type", "application/json")
                .addHeader("x-karza-key", credential?.apiKey!!)
                .send()

            localResponse.message = localHTTPResponse.stringEntity

            val finalResponseJson = JSONObject(localResponse.message)

            var statusCode: Int? = -1
            if (finalResponseJson.has("status-code")) {
                statusCode = finalResponseJson.optInt("status-code", -1)
            } else if (finalResponseJson.has("status")) {
                statusCode = finalResponseJson.optInt("status", -1)
            }
//            localResponse.statusCode = statusCode?.toInt()!!

//            if (statusCode == 101) {
//                localResponse.isSuccess = statusCode == 101
//            }

            localResponse.isSuccess = statusCode == 101

        } else {

            var parseType = enAPIDetail.type
            if (enAPIDetail == EnAPIDetail.KYC_OCR) {

                parseType = docType
            }
            return sampleResponse(parseType)

        }

        return localResponse
    }



    private fun sampleResponse(type: String?): LocalResponse {

        val sampleResponse =  when (type) {

            EnAPIDetail.AUTH_PAN.type -> JSONObject("{\n" +
                    "  \"status-code\": 101,\n" +
                    "  \"request_id\": \"e76d271e-83e6-480f-8298-fcb5a5a73860\",\n" +
                    "  \"result\": {\n" +
                    "    \"name\": \"TANYA KHURANA \"\n" +
                    "  }\n" +
                    "}")

            else -> null

        }

        return LocalResponse().apply {
            message = sampleResponse.toString()
//            statusCode = 101
            isSuccess = true
        }

    }

}