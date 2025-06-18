package com.homefirst.kyc.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.homefirst.kyc.client.DigitapClient
import com.homefirst.kyc.dto.EPAuthRequest
import com.homefirst.kyc.helper.PartnerLogHelper
import com.homefirst.kyc.helper.UserActionStatus
import com.homefirst.kyc.manager.ExternalServiceManager
import com.homefirst.kyc.model.KYCDocument
import com.homefirst.kyc.repository.DocumentRepositoryMaster
import com.homefirst.kyc.repository.ExternalServiceLogRepository
import com.homefirst.kyc.utils.*
import homefirst.utilities.*
import homefirst.utilities.utils.*
import homefirst.utilities.utils.LoggerUtils.log
import org.json.JSONObject
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class KycService(
     val oneResponse: UtilsOneResponse,
     val externalServiceManager : ExternalServiceManager,
     val externalServiceLogRepository : ExternalServiceLogRepository,
     val partnerLogHelper: PartnerLogHelper,
     val digitapClient: DigitapClient,
     val documentRepositoryMaster: DocumentRepositoryMaster,
     val objectMapper: ObjectMapper

) {
    @Throws(Exception::class)
    fun validateAadhaar(
        epAuthRequest: EPAuthRequest,
        aadhaarNumber: String
    ): ResponseEntity<String> {

        val epLogger = partnerLogHelper.Builder(epAuthRequest)

        if (aadhaarNumber.isInvalid()) {
            val msg = "Invalid Aadhaar Number"
            epLogger.setRequestStatus(UserActionStatus.FAILURE).setResponseStatus(201)
                .setServiceName(object {}.javaClass.enclosingMethod.name)
                .setRequestDesc(msg).collectLog()
            return oneResponse.invalidData(msg)
        }

        val eServiceLog = externalServiceManager.logService(
            epAuthRequest.orgId,
            DigitapClient.EnEndPointUrl.PASSPORT_VALIDATION.value,
            EnExternalServiceName.DIGITAP,
            EnExternalServiceType.VALIDATE.value
        )

        eServiceLog ?: run {
            log("validateAadhaar - Failed to add external service log in db")
            val msg = "Failed to validate aadhaar"
            epLogger.setServiceName(object {}.javaClass.enclosingMethod.name)
                .setRequestStatus(UserActionStatus.FAILURE).setResponseStatus(201)
                .setRequestDesc(msg).collectLog()
            return oneResponse.operationFailedResponse(msg)
        }

        val lResponse = digitapClient.aadhaarValidation(aadhaarNumber)

        val responseJson = JSONObject(lResponse.message)

        eServiceLog.let {
            it.responseCode = responseJson.optInt("httpStatus", -1).toString()
            it.updateDatetime = DateTimeUtils.getCurrentDateTimeInIST()
            if (lResponse.isSuccess) it.status = EnUserRequestStatus.SUCCESS.value
            else {
                it.responseMessage = lResponse.message
                it.status = EnUserRequestStatus.FAILED.value
            }
        }

        externalServiceLogRepository.save(eServiceLog)

        if (!lResponse.isSuccess) {
            val msg = "Failed to validate aadhaar"
            epLogger.setRequestStatus(UserActionStatus.FAILURE).setResponseStatus(201)
                .setServiceName(object {}.javaClass.enclosingMethod.name)
                .setRequestDesc(msg).collectLog()
            return oneResponse.operationFailedResponse(msg)
        }

        val gender = responseJson.optJSONObject("result")?.optString("aadhaar_gender", NA)

        if (gender.isInvalid()) {
            val msg = "Failed to validate aadhaar "
            epLogger.setRequestStatus(UserActionStatus.FAILURE).setResponseStatus(201)
                .setServiceName(object {}.javaClass.enclosingMethod.name).setRequestDesc(msg).collectLog()
            return oneResponse.operationFailedResponse(msg)
        }

        var kycDocument = KYCDocument()

        documentRepositoryMaster.kycDocumentRepository.findByDocumentIdAndDocumentType(
            encryptAnyKey(aadhaarNumber), EnDocumentType.AADHAAR.value
        )?.let {
            kycDocument = it
        } ?: run {
            kycDocument.apply {
                orgId = epAuthRequest.orgId
                documentId = encryptAnyKey(aadhaarNumber)
                documentType = EnDocumentType.PAN.value
            }
        }

        kycDocument.userGender = gender
        kycDocument.isValidated = true
        kycDocument.validationData = responseJson.toString()
        kycDocument.isVerified = true
        kycDocument.updateDatetime = DateTimeUtils.getCurrentDateTimeInIST()


        println("kycOn ${objectMapper.writeValueAsString(kycDocument)}")

        documentRepositoryMaster.kycDocumentRepository.save(kycDocument)

        eServiceLog.apply {
            objectId = kycDocument.id
            objectName = MyObject.KYC_DOCUMENT.value
            updateDatetime = DateTimeUtils.getCurrentDateTimeInIST()
        }

        externalServiceLogRepository.save(eServiceLog)

        epLogger.setServiceName(object {}.javaClass.enclosingMethod.name)
            .setResponseStatus(200).setRequestStatus(UserActionStatus.SUCCESS).collectLog()

        kycDocument.documentId = decryptAnyKey(kycDocument.documentId!!)

        return oneResponse.getSuccessResponse(
            JSONObject(objectMapper.writeValueAsString(kycDocument))
        )
    }

    @Throws(Exception::class)
    fun validatePassport(
        epAuthRequest: EPAuthRequest,
        fileNumber: String,
        dob: String
    ): ResponseEntity<String>? {

        val epLogger = partnerLogHelper.Builder(epAuthRequest)

        if (fileNumber.isInvalid()) {
            val msg = "Invalid File Number"
            epLogger.setRequestStatus(UserActionStatus.FAILURE).setResponseStatus(201)
                .setServiceName(object {}.javaClass.enclosingMethod.name)
                .setRequestDesc(msg).collectLog()
            return oneResponse.invalidData(msg)
        }

        if (dob.isInvalid()) {
            val msg = "Invalid dob"
            epLogger.setRequestStatus(UserActionStatus.FAILURE).setResponseStatus(201)
                .setServiceName(object {}.javaClass.enclosingMethod.name)
                .setRequestDesc(msg).collectLog()
            return oneResponse.invalidData(msg)
        }

        val eServiceLog = externalServiceManager.logService(
            epAuthRequest.orgId,
            DigitapClient.EnEndPointUrl.PASSPORT_VALIDATION.value,
            EnExternalServiceName.DIGITAP,
            EnExternalServiceType.VALIDATE.value
        )

        eServiceLog ?: run {
            log("validatePassport - Failed to add external service log in db")
            val msg = "Failed to validate passport"
            epLogger.setServiceName(object {}.javaClass.enclosingMethod.name)
                .setRequestStatus(UserActionStatus.FAILURE).setResponseStatus(201)
                .setRequestDesc(msg).collectLog()
            return oneResponse.operationFailedResponse(msg)
        }

        val lResponse = digitapClient.passportValidation(fileNumber, dob)

        val responseJson = JSONObject(lResponse.message)

        eServiceLog.let {
            it.responseCode = responseJson.optInt("http_response_code", -1).toString()
            it.updateDatetime = DateTimeUtils.getCurrentDateTimeInIST()
            if (lResponse.isSuccess) it.status = EnUserRequestStatus.SUCCESS.value
            else {
                it.responseMessage = lResponse.message
                it.status = EnUserRequestStatus.FAILED.value
            }
        }

        externalServiceLogRepository.save(eServiceLog)

        if (!lResponse.isSuccess) {
            val msg = "Failed to validate passport"
            epLogger.setRequestStatus(UserActionStatus.FAILURE).setResponseStatus(201)
                .setServiceName(object {}.javaClass.enclosingMethod.name)
                .setRequestDesc(msg).collectLog()
            return oneResponse.operationFailedResponse(msg)
        }

        val nameOnPassport = responseJson.optJSONObject("result")?.optString("given_name", NA)
        val passNumber = responseJson.optJSONObject("result")?.optString("passport_number", NA)

        if (passNumber.isInvalid()) {
            val msg = "Failed to validate passport"
            epLogger.setRequestStatus(UserActionStatus.FAILURE).setResponseStatus(201)
                .setServiceName(object {}.javaClass.enclosingMethod.name).setRequestDesc(msg).collectLog()
            return oneResponse.operationFailedResponse(msg)
        }

        var kycDocument = KYCDocument()

        documentRepositoryMaster.kycDocumentRepository.findByDocumentIdAndDocumentType(
            encryptAnyKey(passNumber!!), EnDocumentType.PASSPORT.value
        )?.let {
            kycDocument = it
        } ?: run {
            kycDocument.apply {
                orgId = epAuthRequest.orgId
                documentId = encryptAnyKey(passNumber)
                documentType = EnDocumentType.VOTER_ID.value
            }
        }

        kycDocument.userName = nameOnPassport
        kycDocument.isValidated = true
        kycDocument.validationData = responseJson.toString()
        kycDocument.isVerified = true
        kycDocument.updateDatetime = DateTimeUtils.getCurrentDateTimeInIST()

        documentRepositoryMaster.kycDocumentRepository.save(kycDocument)

        eServiceLog.apply {
            objectId = kycDocument.id
            objectName = MyObject.KYC_DOCUMENT.value
            updateDatetime = DateTimeUtils.getCurrentDateTimeInIST()
        }

        externalServiceLogRepository.save(eServiceLog)

        epLogger.setServiceName(object {}.javaClass.enclosingMethod.name).setResponseStatus(200)
            .setRequestStatus(UserActionStatus.SUCCESS).collectLog()

        kycDocument.documentId = decryptAnyKey(kycDocument.documentId!!)

        return oneResponse.getSuccessResponse(
            JSONObject(objectMapper.writeValueAsString(kycDocument))
        )

    }

}