package algo.vk_monetisation.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

@Configuration
@Profile("wildfly")
public class WildFlyJpaCompatibilityConfiguration {

    @Bean
    public BeanPostProcessor entityManagerFactoryInterfacePostProcessor() {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
                if (bean instanceof LocalContainerEntityManagerFactoryBean emfBean) {
                    emfBean.setEntityManagerFactoryInterface(EntityManagerFactory.class);
                }
                return bean;
            }
        };
    }
}
