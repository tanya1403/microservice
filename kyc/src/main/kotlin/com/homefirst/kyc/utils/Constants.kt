package com.homefirst.kyc.utils

const val AUTHORIZATION = "Authorization"
const val CONTENT_TYPE = "Content-Type"
const val SESSION_PASSCODE = "sessionPasscode"
const val ORG_ID = "orgId"
const val CONTENT_TYPE_FORM_URLENCODED = "application/x-www-form-urlencoded"
const val CONTENT_TYPE_APPLICATION_JSON = "application/json"


enum class EnExternalServiceName(val value: String) {
    DIGITAP("DIGITAP"),
    MSG91("MSG91"),
    KALEYRA("KALEYRA")
}

enum class EnUserRequestStatus(val value: String) {
    CREATED("CREATED"),
    FAILED("FAILED"),
    PARTIALLY_COMPLETED("PARTIALLY_COMPLETED"),
    SUCCESS("SUCCESS");
}


enum class EnExternalServiceType(val value: String) {
    OCR("OCR"),
    VALIDATE("Validate");
}

enum class Errors(val value: String) {
    UNKNOWN("UNKNOWN"),
    FAILED("FAILED"),
    INVALID_CREDENTIALS("INVALID_CREDENTIALS"),
    RESOURCE_NOT_FOUND("RESOURCE_NOT_FOUND"),
    ACCESS_DENIED("ACCESS_DENIED"),
    UNAUTHORIZED_ACCESS("UNAUTHORIZED_ACCESS"),
    DUPLICATE_RECORD("DUPLICATE_RECORD"),
    STRING_TOO_LONG("STRING_TOO_LONG"),
    JSON_PARSER_ERROR("JSON_PARSER_ERROR"),
    OPERATION_FAILED("OPERATION_FAILED"),
    INVALID_DATA("INVALID_DATA"),
    INVALID_REQUEST("INVALID_REQUEST"),
    INCONLUSIVE("INCONCLUSIVE"),
    SERVICE_NOT_FOUND("SERVICE_NOT_FOUND");
}

enum class MyObject(val value: String) {
    DOCUMENT("Document"),
    KYC_DOCUMENT("KYCDocument"),
    BANK_ACCOUNT("BankAccount"),
    E_SIGN_DOCUMENT("ESignDocument"),
    LOAN("Loan"),
    BUREAU_REPORT("BureauReport"),
    BUREAU_REQUEST("BureauRequest"),
    APPLICANT("Applicant"),
    CONNECTOR("Connector"),
    E_STAMP_REQUEST("EStampRequest"),
    CSUB_PAYOUT("CSubPayout"),
    SITE_PHOTO("SitePhotograph"),
    LEAD("Lead"),
    USER("User"),
    ROLE("Role"),
    SALESGROUP("SalesGroup"),
    TEAM("Team"),
    ORGANIZATION("Organization"),
    PARTNER("Partner"),
    LEAD_SOURCE("LeadSource"),
    CAMPAIGN("Campaigns"),
    CROSS_NOTIFICATION("CrossNotification"),
    PROPERTY_INSIGHT("PropertyInsight"),
    CALL_LOG("CallLog"),
    TASK("Task"),
    LEAD_EXPORT("LeadExport"),
    SMS_TEMPLATE("SMSTemplate"),
    USER_REQUEST("cas_UserRequest"),
    SMS_LOG("SMSLog"),
    CONTACT("Contact"),
    NOTIFICATION_TOKEN("cas_NotificationToken"),
    NOTIFICATION("cas_Notification"),
    SF_LOAN_AMORT_REQUEST("SFLoanAmortRequest"),
    PROPERTY("Property"),
    MOBILE_NUMBER_LOOKUP("MobileNumberLookup");

    companion object {
        @Throws(Exception::class)
        operator fun get(value: String): MyObject {
            for (mo: MyObject in values()) {
                if ((mo.value == value)) return mo
            }
            throw Exception("No Object mapped for value: $value")
        }
    }
}

enum class EnDocumentType(val value: String, val s3BucketPath: EnS3BucketPath?, val isDoubleSideDocument: Boolean) {
    AADHAAR("Aadhaar", EnS3BucketPath.AADHAAR_IMAGES, true),
    PAN("PAN", EnS3BucketPath.PAN_IMAGES, false),
    VOTER_ID("VoterId", EnS3BucketPath.VOTER_IMAGES, true),
    PASSPORT("Passport", EnS3BucketPath.PASSPORT_IMAGES, true),
    DRIVING_LICENCE("DrivingLicence", null, false),
    MOBILE("MOBILE", null, false),
    EMAIL("Email", null, false),
    CIBIL_REPORT("CibilReport", null, false),
    GSTIN("GSTIN", null, false),
    BANK_ACCOUNT("BankAccount", null, false),
    CHEQUE("cheque", null, false),
    PASSBOOK("passbook", null, false),
    PROPERTY_INSIGHT_DIGITAL("PropertyInsightDigital", null, false),
    PROPERTY_INSIGHT_LEGAL("PropertyInsightLegal", null, false);

    companion object {
        operator fun get(value: String): EnDocumentType? {
            return values().find { it.value == value }
        }
    }
}
enum class EnS3BucketPath(val stringValue: String) {
    PROPERTY_INSIGHT_REPORTS("HomefirstOne/Property/InsightReport"),
    PROPERTY_INSIGHT_DOCUMENT("HomefirstOne/Property/InsightPropertyDocument"),
    AUDIO_RECORDING("HomefirstOne/Recording/Audio"),
    SIGNED_DOCS("HomefirstOne/ESign/Signed"),
    UNSIGNED_DOCS("HomefirstOne/ESign/Unsigned"),
    DOCUMENT_IMAGES("HomefirstOne/Documents"),
    MASK_IMAGES("HomefirstOne/Documents/Masked"),
    PAN_IMAGES("HomefirstOne/Documents/PAN"),
    AADHAAR_IMAGES("HomefirstOne/Documents/Aadhaar"),
    VOTER_IMAGES("HomefirstOne/Documents/Voter"),
    DIGILOCKER_DOCUMENTS("HomefirstOne/Documents/DigiLocker"),
    PASSPORT_IMAGES("HomefirstOne/Documents/Passport"),
    DRIVING_LICENCE_IMAGES("HomefirstOne/Documents/DrivingLicence"),
    TEMP_A_IMAGES("HomefirstOne/Documents/TempA"),
    CIBIL_DOCS("HomefirstOne/Documents/BureauReport/CibilDocument"),
    LAI_PATH("HomefirstOne/LAI_DOCUMENTS"),
    BANK_ACCOUNT("HomefirstOne/Documents/BankAccount"),
    IDV_DOCS("HomefirstOne/Documents/BureauReport/IdvDocument"),
    ESTAMP("HomefirstOne/EStamp"),
    PROPERTY_SITE_PHOTOGRAPH("HomefirstOne/Property/SitePhotograph"),
    CSUB_PAYOUT("HomefirstOne/Connector/PayoutUploads"),
    BACKUP_OPPORTUNITY("HomefirstOne/SFObject/Opportunity"),
    CO_LENDING_DOCS("HomefirstOne/Extras/CoLending"),
    GENERAL_DOCS("HomefirstOne/Extras/GeneralDocuments"),
    BANNER("external/promotion/banner"),
    AMORT_CALCULATION("HomefirstOne/Extras/Amort"),
    LMS_EXPORT("HomefirstOne/LMS/Exports"),
    PROPERTY_IMAGES("HomefirstOne/Property/PropertyPhotograph"),
    LOGS_SERVER1("HFO/Server1"),
    LOGS_SERVER2("HFO/Server2");
}


