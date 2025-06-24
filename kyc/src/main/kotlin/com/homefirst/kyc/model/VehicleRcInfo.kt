package com.homefirst.kyc.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.homefirst.kyc.utils.DateTimeUtils
import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.GenericGenerator

@Entity
@Table(name = "`VehicleRcInfo`")
class VehicleRcInfo {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(updatable = false, nullable = false)
    var id: String? = null

    @JsonIgnore
    @Column(nullable = false)
    var orgId: String? = null

    @JsonIgnore
    var documentType: String? = null
    var userId: String? = null
    var mobileNumber: String? = null
    var registrationNumber: String? = null
    var engineNumber: String? = null
    var vehicleDescription: String? = null
    var rcRegisteredAt: String? = null
    var bodyDescription: String? = null
    var registeredVehicleColor: String? = null
    var presentAddress: String? = null
    var permanentAddress: String? = null
    var rtoName: String? = null
    var registrationDate: String? = null
    var insuranceCompany: String? = null
    var insuranceValidUpto: String? = null
    var insurancePolicyNumber: String? = null
    var ownerSerialNumber: String? = null
    var registeredOwnerName: String? = null
    var registeredOwnerFathersName: String? = null
    var chassisNumber: String? = null
    var modelMaker: String? = null
    var vahanDbStatusMessage: String? = null
    var fuelDescription: String? = null

    @JsonIgnore
    @Column(columnDefinition = "JSON")
    var rawData: String? = null

    @ColumnDefault("0")
    var isVerified = false

    @ColumnDefault("0")
    var isValidated = false

    @Column(columnDefinition = "DATETIME", updatable = false, nullable = false)
    var createDatetime: String = DateTimeUtils.getCurrentDateTimeInIST()

    @Column(columnDefinition = "DATETIME")
    var updateDatetime: String = DateTimeUtils.getCurrentDateTimeInIST()
}
