package algo.vk_monetisation.eis.jca.smsru;

import jakarta.resource.ResourceException;

import java.io.Serializable;

public class SmsRuConnectionFactoryImpl implements SmsRuConnectionFactory, Serializable {

    private final SmsRuManagedConnectionFactory managedConnectionFactory;

    public SmsRuConnectionFactoryImpl(SmsRuManagedConnectionFactory managedConnectionFactory) {
        this.managedConnectionFactory = managedConnectionFactory;
    }

    @Override
    public SmsRuConnection getConnection() throws ResourceException {
        return managedConnectionFactory.allocateConnection();
    }
}
