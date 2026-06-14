package algo.vk_monetisation.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.Arrays;

public class WildFlyProfileEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        boolean runningInWildFly = environment.getProperty("jboss.home.dir") != null;
        if (!runningInWildFly) {
            return;
        }
        boolean wildflyAlreadyActive = Arrays.asList(environment.getActiveProfiles()).contains("wildfly");
        if (!wildflyAlreadyActive) {
            environment.addActiveProfile("wildfly");
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
