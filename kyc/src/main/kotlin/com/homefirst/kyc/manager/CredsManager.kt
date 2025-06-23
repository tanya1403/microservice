package com.homefirst.kyc.manager

import com.homefirst.kyc.repository.CredsRepository
import com.homefirst.kyc.model.Creds
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Component

enum class EnCredType(val value: String) {
    PRODUCTION("PRODUCTION"),
    UAT("UAT"),
    PRE_PROD("PRE_PROD");
}

enum class EnPartnerName(val value: String) {
    HOMEFIRST("homefirst"),
    GUPSHUP("Gupshup"),
    GOOGLE_MAPS("Google_Maps"),
    SALESFORCE("Salesforce"),
    TEAL_V2("Teal-V2"),
    AMAZON("AWS-RABIT"),
    GOOGLE_DNR("Google_DNR"),
    KALEYRA_SMS("KaleyraSMS"),
    KALEYRA_IO("KaleyraIO"),
    KALEYRA("Kaleyra"),
    DIGITAP("Digitap"),
    KARZA("Karza"),
    DIGILOCKER("DigiLocker"),
    SHLINK("SHLINK"),
    MSG91("MSG91")
}

@Component
class CredsManager(
     val credsRepository: CredsRepository,
     val entityManager: EntityManager
) {

    fun fetchCredentials(
        partnerName: EnPartnerName,
        credType: EnCredType
    ): Creds? {

        val cred =  credsRepository.findByPartnerNameAndCredType(
            partnerName.value,
            credType.value
        )?.apply {
            entityManager.detach(this)
        }

        return cred

    }

}