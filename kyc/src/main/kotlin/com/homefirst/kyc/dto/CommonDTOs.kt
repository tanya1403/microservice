package com.homefirst.kyc.dto

import kotlinx.serialization.Serializable


@Serializable
data class PanAuthRequest (
    val uid: String,
    val pan: String,
    val name: String,
    val dob: String,
    val contactId: String,
    val consent: String
)



