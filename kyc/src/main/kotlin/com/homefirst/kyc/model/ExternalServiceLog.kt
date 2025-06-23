package com.homefirst.kyc.model

import homefirst.utilities.utils.DateTimeUtils
import jakarta.persistence.*
import org.hibernate.annotations.GenericGenerator
import java.util.UUID


@Entity
@Table(name = "`ExternalServiceLog`")
class ExternalServiceLog {

    @Id
    @Column(name = "id")
    var id: String = UUID.randomUUID().toString()

    var orgId: String? = null
    var serviceName: String? = null
    var serviceType: String? = null
    var serviceUrl: String? = null
    var status: String? = null
    var responseCode: String? = null
    var responseMessage: String? = null
    var objectId: String? = null
    var objectName: String? = null
    var documentType: String? = null


    @Column(columnDefinition = "DATETIME", updatable = false, nullable = false)
    var createDatetime: String = DateTimeUtils.getCurrentDateTimeInIST()

    @Column(columnDefinition = "DATETIME")
    var updateDatetime: String? = null

}