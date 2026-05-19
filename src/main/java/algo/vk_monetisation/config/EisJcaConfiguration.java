package algo.vk_monetisation.config;

import algo.vk_monetisation.eis.jca.dadata.DadataConnectionFactory;
import algo.vk_monetisation.eis.jca.dadata.DadataManagedConnectionFactory;
import algo.vk_monetisation.eis.jca.smsru.SmsRuConnectionFactory;
import algo.vk_monetisation.eis.jca.smsru.SmsRuManagedConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EisJcaConfiguration {

    @Bean
    public DadataConnectionFactory dadataConnectionFactory(
            @Value("${app.eis.dadata.api-url}") String apiUrl,
            @Value("${app.eis.dadata.token:}") String token,
            @Value("${app.eis.stub-mode:true}") boolean stubMode) {
        return new DadataManagedConnectionFactory(apiUrl, token, stubMode).buildConnectionFactory();
    }

    @Bean
    public SmsRuConnectionFactory smsRuConnectionFactory(
            @Value("${app.eis.smsru.api-url}") String apiUrl,
            @Value("${app.eis.smsru.api-id:}") String apiId,
            @Value("${app.eis.stub-mode:true}") boolean stubMode) {
        return new SmsRuManagedConnectionFactory(apiUrl, apiId, stubMode).buildConnectionFactory();
    }
}
