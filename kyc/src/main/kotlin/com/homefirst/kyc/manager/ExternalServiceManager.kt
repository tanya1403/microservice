package com.homefirst.kyc.manager



import com.homefirst.kyc.model.ExternalServiceLog
import com.homefirst.kyc.repository.ExternalServiceLogRepository
import com.homefirst.kyc.utils.EnExternalServiceName
import com.homefirst.kyc.utils.EnUserRequestStatus
import homefirst.utilities.utils.DateTimeUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ExternalServiceManager(
    @Autowired val externalServiceLogRepository: ExternalServiceLogRepository
) {

    fun logService(
        orgId: String? = null,
        url: String,
        serviceName: EnExternalServiceName,
        serviceType: String? = null,
        objectId: String? = null,
        objectName: String? = null
    ): ExternalServiceLog? {

        val serviceLog = ExternalServiceLog().apply {

            status = EnUserRequestStatus.CREATED.value

            orgId?.let {
                this.orgId = orgId
            }

            serviceUrl = url
            this.serviceName = serviceName.value

            serviceType?.let {
                this.serviceType = serviceType
            }

            objectId?.let {
                this.objectId = objectId
            }

            objectName?.let {
                this.objectName = objectName
            }

            updateDatetime = DateTimeUtils.getCurrentDateTimeInIST()

        }

        return try {
            externalServiceLogRepository.save(serviceLog)
        } catch (ex: Exception) {
            println("Failed to save ExternalServiceLog: ${ex.message}")
            ex.printStackTrace()
            null
        }

    }

}