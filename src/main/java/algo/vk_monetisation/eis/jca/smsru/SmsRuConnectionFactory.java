package algo.vk_monetisation.eis.jca.smsru;

import jakarta.resource.ResourceException;

public interface SmsRuConnectionFactory {

    SmsRuConnection getConnection() throws ResourceException;
}
