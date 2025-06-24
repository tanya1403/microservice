package com.homefirst.kyc.helper

import com.homefirst.kyc.manager.CredsManager
import com.homefirst.kyc.manager.EnCredType
import com.homefirst.kyc.manager.EnPartnerName
import com.homefirst.kyc.model.Creds
import com.homefirst.kyc.model.ExternalServiceLog
import com.homefirst.kyc.repository.ExternalServiceLogRepository
import com.homefirst.kyc.security.AppProperty
import com.homefirst.kyc.utils.CryptoUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ExternalServiceLogger (
    @Autowired private  val externalServiceLogRepository: ExternalServiceLogRepository,
    @Autowired private val credsManager: CredsManager,
    @Autowired private val appProperty: AppProperty,
    @Autowired private val cryptoUtils: CryptoUtils


    ){
    private var credential: Creds? = credsManager.fetchCredentials(
        EnPartnerName.KARZA,
        if (cryptoUtils.appProperty.isProduction()) EnCredType.PRODUCTION else EnCredType.UAT
    )

    inner class Builder {

        private var esLog = ExternalServiceLog()


        fun setOrgId(value: String?) : ExternalServiceLogger.Builder {
             esLog.orgId = value
            return this
        }
        fun setServiceName(value: String?) : ExternalServiceLogger.Builder {
            esLog.serviceName = value
            return this
        }
        fun setServiceType(value: String?) : ExternalServiceLogger.Builder {
            esLog.serviceType =  value
            return this
        }
        fun setServiceUrl(value: String?) : ExternalServiceLogger.Builder {
            esLog.serviceUrl = "${credential?.apiUrl}${value}"
            return this
        }
        fun setStatus(value: String?) : ExternalServiceLogger.Builder {
            esLog.status = value
            return this
        }
        fun setResponseCode(value: String?) : ExternalServiceLogger.Builder {
            esLog.responseCode = value
            return this
        }
        fun setDocumentType(value: String?) : ExternalServiceLogger.Builder {
            esLog.documentType = value
            return this
        }
        fun setUpdateDatetime(value: String?) : ExternalServiceLogger.Builder {
            esLog.updateDatetime = value
            return this
        }
        fun setObjectId(value: String?) : ExternalServiceLogger.Builder {
            esLog.objectId = value
            return this
        }
        fun setObjectName(value: String?) : ExternalServiceLogger.Builder {
            esLog.objectName = value
            return this
        }
        fun log() {
            esLog.serviceName = credential!!.partnerName
            externalServiceLogRepository.save(esLog)
        }
    }


}