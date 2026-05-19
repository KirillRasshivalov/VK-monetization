package algo.vk_monetisation.eis.jca.dadata;

import jakarta.resource.ResourceException;

import java.io.Serializable;

public class DadataConnectionFactoryImpl implements DadataConnectionFactory, Serializable {

    private final DadataManagedConnectionFactory managedConnectionFactory;

    public DadataConnectionFactoryImpl(DadataManagedConnectionFactory managedConnectionFactory) {
        this.managedConnectionFactory = managedConnectionFactory;
    }

    @Override
    public DadataConnection getConnection() throws ResourceException {
        return managedConnectionFactory.allocateConnection();
    }
}
