package algo.vk_monetisation.eis.jca.dadata;

import jakarta.resource.ResourceException;
import jakarta.resource.spi.ConnectionManager;
import jakarta.resource.spi.ConnectionRequestInfo;
import jakarta.resource.spi.ManagedConnection;
import jakarta.resource.spi.ManagedConnectionFactory;
import jakarta.resource.spi.ValidatingManagedConnectionFactory;

import javax.security.auth.Subject;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Collections;
import java.util.Set;

public class DadataManagedConnectionFactory
        implements ManagedConnectionFactory, ValidatingManagedConnectionFactory, Serializable {

    private final String apiUrl;
    private final String token;
    private final boolean stubMode;
    private PrintWriter logWriter;

    public DadataManagedConnectionFactory(String apiUrl, String token, boolean stubMode) {
        this.apiUrl = apiUrl;
        this.token = token;
        this.stubMode = stubMode;
    }

    public DadataConnectionFactory buildConnectionFactory() {
        return new DadataConnectionFactoryImpl(this);
    }

    @Override
    public ManagedConnection createManagedConnection(Subject subject, ConnectionRequestInfo cxRequestInfo)
            throws ResourceException {
        return new DadataManagedConnection(apiUrl, token, stubMode);
    }

    @Override
    public ManagedConnection matchManagedConnections(Set connections, Subject subject,
                                                     ConnectionRequestInfo cxRequestInfo) {
        if (connections == null || connections.isEmpty()) {
            return null;
        }
        return (ManagedConnection) connections.iterator().next();
    }

    @Override
    public Set getInvalidConnections(Set connectionSet) {
        return Collections.emptySet();
    }

    @Override
    public Object createConnectionFactory() {
        return new DadataConnectionFactoryImpl(this);
    }

    @Override
    public Object createConnectionFactory(ConnectionManager cxManager) {
        return new DadataConnectionFactoryImpl(this);
    }

    @Override
    public void setLogWriter(PrintWriter out) {
        this.logWriter = out;
    }

    @Override
    public PrintWriter getLogWriter() {
        return logWriter;
    }

    DadataConnection allocateConnection() throws ResourceException {
        ManagedConnection mc = createManagedConnection(null, null);
        return (DadataConnection) mc.getConnection(null, null);
    }
}
