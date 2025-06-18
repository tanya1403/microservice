//package com.homefirst.kyc.koin
//
//import com.fasterxml.jackson.databind.ObjectMapper
//import com.homefirst.kyc.client.DigitapClient
//import com.homefirst.kyc.helper.PartnerLogHelper
//import com.homefirst.kyc.manager.CredsManager
//import com.homefirst.kyc.manager.ExternalServiceManager
//import com.homefirst.kyc.netwokring.CommonNetworkingClient
//import com.homefirst.kyc.repository.CredsRepository
//import com.homefirst.kyc.repository.DocumentRepositoryMaster
//import com.homefirst.kyc.repository.ExternalServiceLogRepository
//import com.homefirst.kyc.utils.CryptoUtils
//import homefirst.utilities.utils.UtilsOneResponse
//import org.koin.dsl.module
//import org.springframework.beans.factory.annotation.Autowired
//
//val appModule = module {
//
//    single {CredsRepository()}
//    single { CryptoUtils(get()) }
//    single { CredsManager() }
//    single { CommonNetworkingClient() }
//
//    // Provide UtilsOneResponse as singleton
//    single { UtilsOneResponse() }
//
//    // Provide ExternalServiceLogRepository as singleton
////    single { ExternalServiceLogRepository() }
//
//    // Provide ExternalServiceManager with dependency injected
//    single { ExternalServiceManager(get()) }  // get() will inject ExternalServiceLogRepository
//
//    // Provide PartnerLogHelper as singleton
//    single { PartnerLogHelper(get()) }
//
//    // Provide DigitapClient as singleton
//    single { DigitapClient(get(),get(),get()) }
//
//    // Provide DocumentRepositoryMaster as singleton
//    single { DocumentRepositoryMaster() }
//
//    // Provide ObjectMapper singleton
//    single { ObjectMapper() }
//
//    // Provide KycService with all dependencies injected by Koin
//    single {
//        KycService(
//            oneResponse = get(),
//            externalServiceManager = get(),
//            externalServiceLogRepository = get(),
//            partnerLogHelper = get(),
//            digitapClient = get(),
//            documentRepositoryMaster = get(),
//            objectMapper = get()
//        )
//    }
//}
//
//}
