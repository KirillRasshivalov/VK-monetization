package algo.vk_monetisation.config;

import jakarta.annotation.Resource;
import jakarta.transaction.TransactionManager;
import jakarta.transaction.TransactionSynchronizationRegistry;
import jakarta.transaction.UserTransaction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.sql.DataSource;

@Configuration
@Profile("wildfly")
public class WildFlyJtaConfiguration {

    @Resource(lookup = "java:comp/UserTransaction")
    private UserTransaction userTransaction;

    @Resource(lookup = "java:/TransactionManager")
    private TransactionManager transactionManager;

    @Resource(lookup = "java:comp/TransactionSynchronizationRegistry")
    private TransactionSynchronizationRegistry transactionSynchronizationRegistry;

    @Bean(name = {"transactionManager", "platformTransactionManager"})
    @Primary
    public PlatformTransactionManager jtaTransactionManager() {
        JtaTransactionManager txManager = new JtaTransactionManager(userTransaction, transactionManager);
        txManager.setAllowCustomIsolationLevels(true);
        return txManager;
    }

    @Bean
    public TransactionSynchronizationRegistry transactionSynchronizationRegistry() {
        return transactionSynchronizationRegistry;
    }

    @Bean(name = "dataSource")
    @Primary
    public DataSource mainDataSource(@Value("${spring.datasource.jndi-name}") String jndiName) {
        JndiDataSourceLookup jndiLookup = new JndiDataSourceLookup();
        jndiLookup.setResourceRef(false);
        return jndiLookup.getDataSource(jndiName);
    }

    @Bean(name = "ledgerDataSource")
    public DataSource ledgerDataSource(@Value("${app.tx.ledger.jndi-name}") String jndiName) {
        JndiDataSourceLookup jndiLookup = new JndiDataSourceLookup();
        jndiLookup.setResourceRef(false);
        return jndiLookup.getDataSource(jndiName);
    }
}
