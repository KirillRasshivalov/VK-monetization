package algo.vk_monetisation.eis.jca.smsru;

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

public class SmsRuManagedConnectionFactory
        implements ManagedConnectionFactory, ValidatingManagedConnectionFactory, Serializable {

    private final String apiUrl;
    private final String apiId;
    private final boolean stubMode;
    private PrintWriter logWriter;

    public SmsRuManagedConnectionFactory(String apiUrl, String apiId, boolean stubMode) {
        this.apiUrl = apiUrl;
        this.apiId = apiId;
        this.stubMode = stubMode;
    }

    public SmsRuConnectionFactory buildConnectionFactory() {
        return new SmsRuConnectionFactoryImpl(this);
    }

    @Override
    public ManagedConnection createManagedConnection(Subject subject, ConnectionRequestInfo cxRequestInfo)
            throws ResourceException {
        return new SmsRuManagedConnection(apiUrl, apiId, stubMode);
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
        return new SmsRuConnectionFactoryImpl(this);
    }

    @Override
    public Object createConnectionFactory(ConnectionManager cxManager) {
        return new SmsRuConnectionFactoryImpl(this);
    }

    @Override
    public void setLogWriter(PrintWriter out) {
        this.logWriter = out;
    }

    @Override
    public PrintWriter getLogWriter() {
        return logWriter;
    }

    SmsRuConnection allocateConnection() throws ResourceException {
        ManagedConnection mc = createManagedConnection(null, null);
        return (SmsRuConnection) mc.getConnection(null, null);
    }
}
