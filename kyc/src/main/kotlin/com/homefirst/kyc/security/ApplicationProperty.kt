package com.homefirst.kyc.security


import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseApp.*
import com.google.firebase.FirebaseOptions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.core.io.ClassPathResource
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.io.IOException
import java.util.*

enum class EnvProfile(
    val value : String) {
    DEV("dev"),
    STAGING("staging"),
    UAT("uat"),
    PROD("prod"),
    LOCAL_PROD("local_prod")
}

@Configuration
@EnableAsync
class AppProperty {

//    @Bean
//    @Throws(IOException::class)
//    fun firebaseInstance(): FirebaseApp {
//        val defaultApp = getApps().find { it.name == DEFAULT_APP_NAME }
//
//        return if (defaultApp != null) {
//            defaultApp
//        } else {
//            val googleCredentials = GoogleCredentials
//                .fromStream(ClassPathResource(firebaseConfig).inputStream)
//
//            val firebaseOptions = FirebaseOptions
//                .builder()
//                .setCredentials(googleCredentials)
//                .build()
//
//            initializeApp(firebaseOptions)
//        }
//    }


//    @Bean(name = [THREAD_POOL_TASK_EXECUTOR])
//    fun threadPoolTaskExecutor(): Executor {
//        val executor = ThreadPoolTaskExecutor().apply {
//            corePoolSize = 1
//            maxPoolSize = 20
//            queueCapacity = 100
//            setThreadNamePrefix("Async-Thread-")
//            initialize()
//        }
//
//        // Wrap the executor to propagate the SecurityContext
//        return DelegatingSecurityContextAsyncTaskExecutor(executor)
//    }

//    @Bean
//    fun passwordEncoder(): BCryptPasswordEncoder {
//        return BCryptPasswordEncoder()
//    }
//
//    @Bean
//    fun authenticationManager(
//        authenticationConfiguration: AuthenticationConfiguration
//    ): AuthenticationManager? {
//        return authenticationConfiguration.authenticationManager
//    }

    @Autowired
    lateinit var environment: Environment

    @Value("\${spring.profiles.active}")
    lateinit var activeProfile : String

    @Value("\${application.flags.isStrictProduction}")
    var isStrictProduction : Boolean = false

    fun isProduction() = activeProfile == EnvProfile.PROD.value

    fun isLocalProduction() = activeProfile == EnvProfile.LOCAL_PROD.value

    fun isUAT() = activeProfile == EnvProfile.UAT.value

    fun isStaging() = activeProfile == EnvProfile.DEV.value

    fun isDev(): Boolean {
        return !(isProduction() && isUAT() && isStaging())
    }

    @Value("\${application.key.crownPasscode}")
    lateinit var crownPasscode: String

    @Value("\${application.key.salt}")
    lateinit var salt: String

    @Value("\${application.flags.isSalesforceLive}")
    var isSalesforceLive: Boolean = false

    @Value("\${application.jwt.secretKey}")
    lateinit var jwtSecretKey: String

    @Value("\${application.jwt.header}")
    lateinit var  jwtHeader: String

    @Value("\${application.jwt.expiration}")
    val jwtExpiration: Long = 3600000

    @Value("\${application.key.mamasSalt}")
    lateinit var  mamasSalt: String

    @Value("\${application.key.mamasSpaghetti}")
    lateinit var  mamasSpaghetti: String

    @Value("\${application.key.sourcePasscode}")
    lateinit var  sourcePasscode: String

    @Value("\${application.path.files}")
    lateinit var  filePath: String

    @Value("\${application.key.hfoOrgId}")
    lateinit var  hfoOrgId: String

    @Value("\${application.s3Bucket.name}")
    lateinit var s3BucketName: String

    @Value("\${application.s3LogBucket.name}")
    lateinit var s3LogBucketName: String

    @Value("\${application.s3Bucket.region}")
    lateinit var s3BucketRegion: String

    @Value("\${application.path.fileIdentifierURL}")
    lateinit var fileIdentifier: String

    @Value("\${application.path.hfoFileIdentifierURL}")
    lateinit var hfoFileIdentifierURL: String

    @Value("\${application.url.kaleyraBaseUrl}")
    lateinit var kaleyraBaseUrl: String

    @Value("\${app.firebase.config}")
    lateinit var firebaseConfig: String

    @Value("\${application.flags.isNotificationLive}")
    var isNotificationLive : Boolean = false

    @Value("\${application.key.runScheduler}")
    var  runScheduler: Boolean = false

    @Value("\${application.key.backupLog}")
    var  backupLog: Boolean = false

    @Value("\${application.key.mockedMobileNumber}")
    lateinit var  mockedMobileNumber: String

    @Value("\${application.key.google_group_orcas}")
    lateinit var  googleGroupOrcasCallbackUrl: String

    @Value("\${application.google.client-id}")
    lateinit var  googleClientId: String

}
