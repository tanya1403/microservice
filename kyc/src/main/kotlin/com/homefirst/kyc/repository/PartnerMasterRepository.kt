package com.homefirst.kyc.repository

import com.homefirst.kyc.model.ExternalServiceLog
import com.homefirst.kyc.model.Creds
import com.homefirst.kyc.model.PartnerLog
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Component("microservicePartner")
class PartnerMasterRepository(
    @Autowired val partnerLogRepository: PartnerLogRepository,
    @Autowired val externalServiceLogRepository: ExternalServiceLogRepository

)

@Repository
interface PartnerLogRepository : JpaRepository<PartnerLog, Int>

@Repository
interface ExternalServiceLogRepository : JpaRepository<ExternalServiceLog, String>

@Repository
interface CredsRepository : JpaRepository<Creds, String> {

    @Transactional(readOnly = true)
    @Query("from Creds where partnerName = :partnerName and credType = :credType and isValid = true")
    fun findByPartnerNameAndCredType(partnerName: String, credType: String): Creds?

}