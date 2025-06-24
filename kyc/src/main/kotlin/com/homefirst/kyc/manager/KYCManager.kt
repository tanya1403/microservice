package com.homefirst.kyc.manager

import com.homefirst.kyc.client.KarzaClient
import com.homefirst.kyc.dto.EPAuthRequest
import com.homefirst.kyc.helper.ExternalServiceLogger
import com.homefirst.kyc.model.KYCDocument
import com.homefirst.kyc.model.VehicleRcInfo
import com.homefirst.kyc.repository.DocumentRepositoryMaster
import com.homefirst.kyc.utils.*
import com.homefirst.kyc.utils.LoggerUtils.log
import org.json.JSONObject
import org.springframework.beans.BeanUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component


@Component
class KYCManager(
    @Autowired private val karzaClient: KarzaClient,
    @Autowired val externalServiceLogger: ExternalServiceLogger,
    @Autowired private val cryptoUtils: CryptoUtils,
    @Autowired val documentRepositoryMaster: DocumentRepositoryMaster,
    ) {


    companion object {
        const val KARZA_VALID = "Valid"
    }

    fun authenticatePan(epLogger: EPAuthRequest, panNumber: String): LocalResponse {

        val esLogger = externalServiceLogger.Builder().apply {
            setDocumentType(epLogger.orgId)
            setDocumentType(KarzaClient.EnAPIDetail.AUTH_PAN.type)
            setServiceUrl(KarzaClient.EnAPIDetail.AUTH_PAN.endPoint)
            setStatus(EnUserRequestStatus.CREATED.value).log()
        }

        var apiCallResponse = LocalResponse()

        if (!panNumber.isValidPan()) {
            apiCallResponse.message = "Invalid pan number"
            apiCallResponse.isSuccess = false
            return apiCallResponse
        }

        val panReqBody = JSONObject().apply {
            put("consent", "Y")
            put("pan", panNumber)
        }

        apiCallResponse = karzaClient.clientAPICall(
            KarzaClient.EnAPIDetail.AUTH_PAN, null, panReqBody
        )

//        esLogger.setResponseCode(apiCallResponse.statusCode.toString())
        esLogger.setServiceType(EnExternalServiceType.AUTHENTICATE.value)

        if (!apiCallResponse.isSuccess) {
            esLogger.apply {
                setStatus(EnUserRequestStatus.FAILED.value)
                setUpdateDatetime(DateTimeUtils.getCurrentDateTimeInIST()).log()
            }
            apiCallResponse.message = "Failed to authenticate pan"
            apiCallResponse.isSuccess = false
            return apiCallResponse
        }

        esLogger.apply {
            setStatus(EnUserRequestStatus.SUCCESS.value)
            setUpdateDatetime(DateTimeUtils.getCurrentDateTimeInIST()).log()
        }

        log("authenticatePan - service log updated")

        val finalJsonResponse = try {
            JSONObject(apiCallResponse.message)
        } catch (e: Exception) {
            apiCallResponse.isSuccess = false
            apiCallResponse.message = "Invalid JSON response from PAN API"
            return apiCallResponse
        }

        val name = finalJsonResponse.optJSONObject("result")?.optString("name")
        if (name.isNullOrBlank()) {
            apiCallResponse.message = "Failed to authenticate pan - name not found"
            apiCallResponse.isSuccess = false
            return apiCallResponse
        }

        val encryptedPan = cryptoUtils.encryptAes(panNumber)

        val kycDocument = KYCDocument().apply {
            orgId = epLogger.orgId
            documentId = encryptedPan
            documentType = KarzaClient.EnAPIDetail.AUTH_PAN.type
            userName = name
            isVerified = true
        }

        // Update existing document if found
        documentRepositoryMaster.kycDocumentRepository.findByDocumentIdAndDocumentType(
            encryptedPan,
            KarzaClient.EnAPIDetail.AUTH_PAN.type
        )?.let {
            kycDocument.id = it.id
            BeanUtils.copyProperties(it, kycDocument)
        }

        documentRepositoryMaster.kycDocumentRepository.save(kycDocument)

        val nKycDocument = KYCDocument().apply {
            BeanUtils.copyProperties(kycDocument, this)
            documentId = cryptoUtils.decryptAes(kycDocument.documentId!!)
        }

        esLogger.apply {
            setObjectId(kycDocument.id)
            setObjectName(EnMyObject.KYC_DOCUMENT.value)
            setUpdateDatetime(DateTimeUtils.getCurrentDateTimeInIST()).log()
        }

        // Convert to JSON properly using kotlinx.serialization or another method
        val responseJson = try {
            JSONObject(
                mapOf(
                    "orgId" to nKycDocument.orgId,
                    "documentId" to nKycDocument.documentId,
                    "documentType" to nKycDocument.documentType,
                    "userName" to nKycDocument.userName,
                    "isVerified" to nKycDocument.isVerified
                )
            ).toString()
        } catch (e: Exception) {
            "{}"
        }

        apiCallResponse.message = responseJson
        apiCallResponse.isSuccess = true
        return apiCallResponse
    }

    fun authenticateVehicleRC(
        epLogger: EPAuthRequest,
        regNumber: String
    ): LocalResponse {

        val esLogger = externalServiceLogger.Builder()

        esLogger.apply {
            setDocumentType(epLogger.orgId)
            setDocumentType(KarzaClient.EnAPIDetail.AUTH_VEHICLE_RC.type)
            setServiceUrl(KarzaClient.EnAPIDetail.AUTH_VEHICLE_RC.endPoint)
            setStatus(EnUserRequestStatus.CREATED.value).log()
        }

        var apiCallResponse = LocalResponse()

        if (!regNumber.isValidRcNumber()) {
            val msg = "Invalid registration RC number "
            apiCallResponse.message = msg
            apiCallResponse.isSuccess = false
            return apiCallResponse
        }

        val requestBody = JSONObject().apply {
            put("reg_no", regNumber)
            put("consent", "Y")
        }

        apiCallResponse = karzaClient.clientAPICall(KarzaClient.EnAPIDetail.AUTH_VEHICLE_RC, null, requestBody)

//        esLogger.setResponseCode(apiCallResponse.statusCode.toString())
        esLogger.setServiceType(EnExternalServiceType.AUTHENTICATE.value)

        if (!apiCallResponse.isSuccess) {
            esLogger.setStatus(EnUserRequestStatus.FAILED.value)
            esLogger.setUpdateDatetime(DateTimeUtils.getCurrentDateTimeInIST()).log()
            val msg = "Failed to authenticate vehicle rc "
            apiCallResponse.message = msg
            apiCallResponse.isSuccess = false
            return apiCallResponse
        }


        esLogger.setStatus(EnUserRequestStatus.SUCCESS.value)
        esLogger.setUpdateDatetime(DateTimeUtils.getCurrentDateTimeInIST()).log()
        log("authenticateVehicleRC - service log updated")

        val finalJsonResponse = JSONObject(apiCallResponse.message)

        val vehicleRcInfo = parseVehicleRcAuthJson(finalJsonResponse) ?: run {
            log("authenticateVehicleRC - No valid request found for parsing karza response")
            val msg = "Failed to authenticate vehicle rc "
            apiCallResponse.message = msg
            apiCallResponse.isSuccess = false
            return apiCallResponse
        }

        documentRepositoryMaster.vehicleRcDocumentRepository.findByRegistrationNumber(regNumber)?.let { existingRc ->
            vehicleRcInfo.id = existingRc.id
            BeanUtils.copyProperties(vehicleRcInfo, existingRc)
        } ?: run {
            vehicleRcInfo.apply {
                this.registrationNumber = regNumber
                documentType = KarzaClient.EnAPIDetail.AUTH_VEHICLE_RC.type
                orgId = epLogger.orgId
            }
        }

        vehicleRcInfo.isVerified = true
        documentRepositoryMaster.vehicleRcDocumentRepository.save(vehicleRcInfo)

        esLogger.setObjectId(vehicleRcInfo.id)
        esLogger.setObjectName(EnMyObject.VEHICLE_RC_INFO.value)
        esLogger.setUpdateDatetime(DateTimeUtils.getCurrentDateTimeInIST()).log()

        apiCallResponse.message = JSONObject(vehicleRcInfo).toString()
        return apiCallResponse
    }




    fun parseVehicleRcAuthJson(responseObject: JSONObject): VehicleRcInfo? {
        val rcJsonResponse = responseObject.optJSONObject("result")

        return rcJsonResponse?.run {
            VehicleRcInfo().apply {
                engineNumber = optString("rc_eng_no", NA)
                vehicleDescription = optString("rc_vh_class_desc", NA)
                presentAddress = optString("rc_present_address", NA)
                permanentAddress = optString("rc_permanent_address", NA)
                mobileNumber = optString("rc_mobile_no", NA)
                registrationDate = optString("rc_regn_dt", NA)
                rcRegisteredAt = optString("rc_registered_at", NA)
                registrationNumber = optString("rc_regn_no", NA)
                registeredVehicleColor = optString("rc_color", NA)
                registeredOwnerName = optString("rc_owner_name", NA)
                ownerSerialNumber = optString("rc_owner_sr", NA)
                registeredOwnerFathersName = optString("rc_f_name", NA)
                bodyDescription = optString("rc_body_type_desc", NA)
                chassisNumber = optString("rc_chasi_no", NA)
                modelMaker = optString("rc_maker_model", NA)
                insurancePolicyNumber = optString("rc_insurance_policy_no", NA)
                insuranceValidUpto = optString("rc_insurance_upto", NA)
                fuelDescription = optString("rc_fuel_desc", NA)
                vahanDbStatusMessage = optString("stautsMessage", NA)
                rawData = rcJsonResponse.toString()
            }
        }
    }


    fun String?.isValidPan(): Boolean {
        val regex = "^[A-Za-z]{5}\\d{4}[A-Za-z]{1}\$".toRegex()
        return this.isNotNullOrNA() && this!!.matches(regex)
    }

    fun String?.isValidRcNumber(): Boolean {
        val regex = "^[A-Z]{2}\\d[A-Z]{2}\\d{4}\$|^[A-Z]{2}\\d{2}[A-Z0-9]{2}\\d{3,4}\$|^[A-Z]{2}\\d{2}[A-Z]\\d{4}\$|^[A-Z]{2}\\d{6}\$|^[A-Z]{3}\\d{4}|^[A-Z]{2}\\d{1}[A-Z]{3}\\d{3,4}\$|^[A-Z]{2}\\d{1}[A-Z]{1}\\d{4}|^[A-Z]{2}\\d{2}[A-Z]{1}\\d{3}\$|^[A-Z]{2}\\d[A-Z]{3}\\d{4}\$|^[A-Z]{2}\\d{2}[A-Z]{1,2}\\d{2}\$|^[0-9]{2}[B,H]{2}[0-9]{4}[A-Z]{1,2}\$".toRegex()
        return this.isNotNullOrNA() && this!!.matches(regex)
    }
}




