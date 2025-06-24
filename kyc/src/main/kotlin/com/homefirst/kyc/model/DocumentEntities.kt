package com.homefirst.kyc.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.homefirst.kyc.utils.DateTimeUtils.getCurrentDateTimeInIST
import com.homefirst.kyc.utils.MyObject
import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault
import java.util.*

@Entity
@Table(name = "`Attachment`")
class Attachment {
    @Id
    @GeneratedValue(generator = "UUID")
    @Column(updatable = false, nullable = false)
    var id: String? = null

    @Column(nullable = false)
    var objectId: String? = null

    @Column(nullable = false)
    var objectType: String? = null

    @Column(nullable = false)
    var fileName: String? = null

    var contentType: String? = null

    @Column(nullable = false)
    var attachementType: String? = null

    var fileIdentifier: String? = null

    @Column(columnDefinition = "DATETIME", updatable = false, nullable = false)
    var createDatetime = getCurrentDateTimeInIST()

    @Column(columnDefinition = "DATETIME")
    var updateDatetime = getCurrentDateTimeInIST()

}

@Entity
@Table(name = "`Document`")
class Document {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(updatable = false, nullable = false)
    var id: String? = null

    @Column(nullable = false)
    var documentType: String? = null

    var otherDocumentType: String? = null

    var documentNumber: String? = null

    var documentDetailID: String? = null

    var documentDetailObject: String? = null

    var tag: String? = null

    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "attachmentId", referencedColumnName = "id")
    var attachment: Attachment? = null

    @Column(columnDefinition = "DATETIME", updatable = false, nullable = false)
    var createDatetime: String = getCurrentDateTimeInIST()

    @Column(columnDefinition = "DATETIME")
    var updateDatetime: String = getCurrentDateTimeInIST()

    fun createDocument(kycDocument: KYCDocument): Document {

        attachment ?: run {
            attachment = kycDocument.attachment
        }
        documentType = kycDocument.documentType
        documentDetailID = kycDocument.id
        documentNumber = kycDocument.documentId
        tag = MyObject.KYC_DOCUMENT.value
        return this
    }

}

@Entity
@Table(name = "`KYCDocument`")
class KYCDocument {

    @Id
    @Column(name = "id")
    var id: String = UUID.randomUUID().toString()

    @Column(nullable = false)
    var orgId: String? = null

    @JsonIgnore
    var userId: String? = null

    var mobileNumber: String? = null

    @Column(nullable = false)
    var documentType: String? = null

    @Column(nullable = false)
    var documentId: String? = null

    @JsonIgnore
    @ManyToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "attachmentId", referencedColumnName = "id")
    var attachment: Attachment? = null

    /* Represents if the KYC Document is verified */
    @ColumnDefault("0")
    var isVerified = false

    /* Represents if the KYC Document is valid */
    @ColumnDefault("0")
    var isValidated = false

    var userName: String? = null
    var userGender: String? = null
    var userDob: String? = null
    var userImageUrl: String? = null

    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "addressId", referencedColumnName = "id")
    var address: Address? = null

    @Column(columnDefinition = "JSON")
    var rawData: String? = null

    @Column(columnDefinition = "JSON")
    var validationData: String? = null

    @Column(columnDefinition = "DATETIME", updatable = false, nullable = false)
    var createDatetime: String = getCurrentDateTimeInIST()

    @Column(columnDefinition = "DATETIME")
    var updateDatetime: String = getCurrentDateTimeInIST()

    @JsonIgnore
    @Transient
    var maskedFrontImageUrl: String? = null

    @JsonIgnore
    @Transient
    var maskedBackImageUrl: String? = null

}