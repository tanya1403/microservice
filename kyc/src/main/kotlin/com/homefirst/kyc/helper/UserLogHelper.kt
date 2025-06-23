package com.homefirst.kyc.helper

import com.homefirst.kyc.dto.EPAuthRequest
import com.homefirst.kyc.repository.PartnerMasterRepository
import com.homefirst.kyc.model.PartnerLog
import com.homefirst.kyc.utils.THREAD_POOL_TASK_EXECUTOR
import homefirst.utilities.utils.LoggerUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.stereotype.Component

enum class UserActionStatus {
    SUCCESS, FAILURE
}

@EnableAsync
@Component
class PartnerLogHelper(
    @Autowired private val partnerMasterRepository: PartnerMasterRepository
) {

    companion object {
        var partnerLogs = ArrayList<PartnerLog>()
    }

    inner class Builder() {

        private var epLog = PartnerLog()

        constructor(epAuthRequest: EPAuthRequest): this() {

            epAuthRequest.run {
                epLog.orgId = orgId
                epLog.endpoint = requestUri
                epLog.ipAddress = ipAddress
            }

        }

        fun setOrgId(value: String?) : Builder {
            epLog.orgId = value
            return this
        }

        fun setEndpoint(value: String?) : Builder {
            epLog.endpoint = value
            return this
        }

        fun setRequestDesc(value: String?) : Builder {
            epLog.requestDesc = value
            return this
        }

        fun setRequestStatus(value: UserActionStatus) : Builder {
            epLog.requestStatus = value.name
            return this
        }

        fun setIPAddress(value: String?) : Builder {
            epLog.ipAddress = value
            return this
        }

        fun setResponseStatus(value: Int?) : Builder {
            epLog.responseStatus = value
            return this
        }

        fun setServiceName(value: String?) : Builder {
            value?.let {
                epLog.serviceName = value
            }
            return this
        }

        fun collectLog() {
            partnerLogs.add(epLog)
        }

//        @Async(THREAD_POOL_TASK_EXECUTOR)
        fun syncLog() {

            LoggerUtils.log("Total partner log sync data: ${partnerLogs.size}")

            if (partnerLogs.isNotEmpty()){
                partnerMasterRepository.partnerLogRepository.saveAll(partnerLogs)
                LoggerUtils.log("Total partner log synced successfully: ${partnerLogs.size}")
                partnerLogs.clear()
            }

        }

        @Async(THREAD_POOL_TASK_EXECUTOR)
        fun log() {
            partnerMasterRepository.partnerLogRepository.save(epLog)
        }

    }

}
