package algo.vk_monetisation.config;

import algo.vk_monetisation.jca.FnsConnectionFactory;
import algo.vk_monetisation.jca.FnsConnectionFactoryImpl;
import algo.vk_monetisation.jca.FnsManagedConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class JcaFnsConfiguration {

    @Bean
    public FnsManagedConnectionFactory fnsManagedConnectionFactory() {
        log.info("Настройка Managed Connection Factory для демо-стенда ФНС");
        FnsManagedConnectionFactory mcf = new FnsManagedConnectionFactory();
        mcf.setFnsServerUrl("https://api.sbis.ru/vok-demo");
        mcf.setConnectionTimeout(5000);
        return mcf;
    }

    @Bean
    public FnsConnectionFactory fnsConnectionFactory(FnsManagedConnectionFactory mcf) {
        log.info("Создание Connection Factory");
        return new FnsConnectionFactoryImpl(mcf);
    }
}