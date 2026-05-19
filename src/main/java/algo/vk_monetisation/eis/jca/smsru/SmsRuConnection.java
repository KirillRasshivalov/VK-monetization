package algo.vk_monetisation.eis.jca.smsru;

import jakarta.resource.ResourceException;

public interface SmsRuConnection extends AutoCloseable {

    SmsRuSendResponse sendSms(SmsRuSendRequest request) throws ResourceException;

    @Override
    void close();
}
