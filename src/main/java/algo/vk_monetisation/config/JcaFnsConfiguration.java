package algo.vk_monetisation.config;

import algo.vk_monetisation.jca.adapter.FnsVerificationResourceAdapter;
import algo.vk_monetisation.jca.connection.FnsConnectionFactory;
import algo.vk_monetisation.jca.connection.FnsConnectionFactoryImpl;
import algo.vk_monetisation.jca.connection.FnsManagedConnectionFactory;
import jakarta.resource.spi.ConnectionManager;
import jakarta.resource.spi.ManagedConnectionFactory;
import jakarta.resource.spi.ConnectionRequestInfo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import lombok.extern.slf4j.Slf4j;


@Configuration
@Slf4j
public class JcaFnsConfiguration {


    @Bean
    public FnsVerificationResourceAdapter fnsResourceAdapter() {
        log.info("Инициализация FNS адептера для проверки ИНН");
        return new FnsVerificationResourceAdapter();
    }

    @Bean
    public FnsManagedConnectionFactory fnsManagedConnectionFactory() {
        log.info("Создание Managed Connection Factory для FNS");
        FnsManagedConnectionFactory mcf = new FnsManagedConnectionFactory();
        mcf.setFnsServerUrl("https://api-fns.example.com");
        mcf.setApiKey("default-api-key");
        mcf.setConnectionTimeout(5000);
        return mcf;
    }

    @Bean
    public FnsConnectionFactory fnsConnectionFactory(FnsManagedConnectionFactory mcf) {
        log.info("Создаем Connection Factory для приложения с использованием Managed Connection Factory");
        ConnectionManager cm = new SimpleConnectionManager();
        return new FnsConnectionFactoryImpl(mcf, cm);
    }

    private static class SimpleConnectionManager implements ConnectionManager {
        @Override
        public Object allocateConnection(ManagedConnectionFactory mcf,
                ConnectionRequestInfo cxRequestInfo) {
            return null;
        }
    }
}


