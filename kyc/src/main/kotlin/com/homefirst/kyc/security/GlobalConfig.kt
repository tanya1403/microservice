package com.homefirst.kyc.security

import jakarta.persistence.EntityManagerFactory
import net.ttddyy.dsproxy.listener.logging.SLF4JLogLevel
import net.ttddyy.dsproxy.listener.logging.SLF4JQueryLoggingListener
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder
import org.jetbrains.annotations.NotNull
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.TransactionException
import org.springframework.transaction.TransactionStatus
import javax.sql.DataSource


@Configuration
class DatabaseConfig {

    @Value("\${spring.datasource.url.readWrite}")
    lateinit var dbUrlReadWrite: String

    @Value("\${spring.datasource.username}")
    lateinit var dbUsername: String

    @Value("\${spring.datasource.password}")
    lateinit var dbPassword: String

    @Value("\${spring.datasource.url.readOnly}")
    lateinit var dbUrlReadOnly: String

    @Value("\${spring.datasource.rUsername}")
    lateinit var dbrUsername: String

    @Value("\${spring.datasource.rPassword}")
    lateinit var dbrPassword: String

    @Bean
    fun readWriteConfiguration(): DataSource {

        return DataSourceBuilder
            .create()
            .url(dbUrlReadWrite)
            .password(dbPassword)
            .username(dbUsername)
            .build()

    }

    @Bean
    fun readOnlyConfiguration(): DataSource {

        return DataSourceBuilder
            .create()
            .url(dbUrlReadOnly)
            .password(dbrPassword)
            .username(dbrUsername)
            .build()

    }

    @Bean
    @Primary
    fun routingDataSource(): DataSource {
        return TransactionRoutingDataSource(
            loggingProxy("read_write", readWriteConfiguration()),
            loggingProxy("read_only", readOnlyConfiguration())
        )
    }

    private fun loggingProxy(name: String, dataSource: DataSource): DataSource {
        val loggingListener = SLF4JQueryLoggingListener()
        loggingListener.logLevel = SLF4JLogLevel.DEBUG
        loggingListener.setLogger(name)
        loggingListener.setWriteConnectionId(false)
        return ProxyDataSourceBuilder.create(dataSource).name(name).listener(loggingListener).build()
    }

    @Bean
    @ConditionalOnMissingBean(name = ["entityManagerFactory"])
    fun entityManagerFactory(
        builder: EntityManagerFactoryBuilder
    ): LocalContainerEntityManagerFactoryBean {
        return builder
            .dataSource(routingDataSource())
            .packages("com.homefirst.kyc","homefirst.utilities")
            .build()
    }


    @Bean
    @Primary
    fun transactionManager(
        @Qualifier("jpaTxManager") wrapped: PlatformTransactionManager?
    ): ReplicaAwareTransactionManager? {

        return wrapped?.let { ReplicaAwareTransactionManager(it) }

    }

    @Bean(name = ["jpaTxManager"])
    fun jpaTransactionManager(emf: EntityManagerFactory?): PlatformTransactionManager {
        return JpaTransactionManager(emf!!)
    }
}


class ReplicaAwareTransactionManager(
    private val wrapped: PlatformTransactionManager
) : PlatformTransactionManager {

    @NotNull
    @Throws(TransactionException::class)
    override fun getTransaction(definition: TransactionDefinition?): TransactionStatus {

        try {

            TransactionRoutingDataSource
                .setReadonlyDataSource(definition != null && definition.isReadOnly)

//            println("ReplicaAwareTransactionManager - Transaction : isReadOnly: ${definition?.isReadOnly} | name: ${definition?.name}")

            return wrapped.getTransaction(definition)

        } finally {

            TransactionRoutingDataSource.unload()

        }

    }

    @Throws(TransactionException::class)
    override fun commit(status: TransactionStatus) {
        wrapped.commit(status)
    }

    @Throws(TransactionException::class)
    override fun rollback(status: TransactionStatus) {
        wrapped.rollback(status)
    }

}

class TransactionRoutingDataSource(master: DataSource, slave: DataSource) :
    AbstractRoutingDataSource() {
    init {
        val dataSources: MutableMap<Any, Any> = HashMap()
        dataSources[DataSourceType.READ_WRITE] = master
        dataSources[DataSourceType.READ_ONLY] = slave
        super.setTargetDataSources(dataSources)
        super.setDefaultTargetDataSource(master)
        super.afterPropertiesSet()
    }

    override fun determineCurrentLookupKey(): Any? {
        return currentDataSource.get()
    }

    private enum class DataSourceType {
        READ_ONLY,
        READ_WRITE
    }

    companion object {
        private val currentDataSource = ThreadLocal<DataSourceType>()
        fun setReadonlyDataSource(isReadonly: Boolean) {
            currentDataSource.set(if (isReadonly) DataSourceType.READ_ONLY else DataSourceType.READ_WRITE)
        }

        fun unload() {
            currentDataSource.remove()
        }
    }
}