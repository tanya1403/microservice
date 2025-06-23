package com.homefirst.kyc.repository

import com.homefirst.kyc.model.KYCDocument
import com.homefirst.kyc.model.VehicleRcInfo
import org.springframework.transaction.annotation.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository

@Component
@Transactional
class DocumentRepositoryMaster(
    @Autowired val kycDocumentRepository: KYCDocumentRepository,
    @Autowired val vehicleRcDocumentRepository : VehicleRcDocumentRepository
)

@Repository
interface KYCDocumentRepository : JpaRepository<KYCDocument, String> {

    @Transactional(readOnly = true)
    fun findByDocumentIdAndDocumentType(documentId: String?, documentType: String?): KYCDocument?

}

@Repository
interface VehicleRcDocumentRepository : JpaRepository<VehicleRcInfo, String> {

    @Transactional(readOnly = true)
    fun findByRegistrationNumber(regNumber: String?): VehicleRcInfo?
}