package com.homefirst.kyc.model

import homefirst.utilities.utils.DateTimeUtils.getCurrentDateTimeInIST
import homefirst.utilities.utils.NA
import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault


@Entity
@Table(name = "`PartnerLog`")
class PartnerLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    var id: String? = null

    @Column(nullable = false)
    var orgId: String? = null

    var endpoint: String? = null
    var ipAddress: String? = null
    var requestDesc: String? = null
    var requestStatus: String? = null
    var serviceName = NA
    var responseStatus: Int? = -1

    @Column(columnDefinition = "DATETIME", updatable = false, nullable = false)
    var datetime: String = getCurrentDateTimeInIST()

}

@Entity
@Table(name = "`Creds`")
class Creds {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    var id: String? = null

    @Column(nullable = false)
    var partnerName: String? = null
    var credType: String? = null
    var username: String? = null
    var password: String? = null
    var memberId: String? = null
    var memberPasscode: String? = null
    var salt: String? = null
    var apiKey: String? = null
    var apiUrl: String? = null

    @ColumnDefault("1")
    var isValid = true

    @ColumnDefault("0")
    var isEncrypted = false

    @Column(columnDefinition = "DATETIME", updatable = false, nullable = false)
    var createDatetime = getCurrentDateTimeInIST()

    @Column(columnDefinition = "DATETIME")
    var updateDatetime = getCurrentDateTimeInIST()

}