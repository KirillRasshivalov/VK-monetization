package algo.vk_monetisation.eis.jca.dadata;

import jakarta.resource.ResourceException;
import jakarta.resource.spi.ConnectionEventListener;
import jakarta.resource.spi.ConnectionRequestInfo;
import jakarta.resource.spi.LocalTransaction;
import jakarta.resource.spi.ManagedConnection;
import jakarta.resource.spi.ManagedConnectionMetaData;
import javax.transaction.xa.XAResource;

import javax.security.auth.Subject;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Set;

public class DadataManagedConnection implements ManagedConnection {

    private final DadataConnectionImpl physicalConnection;
    private PrintWriter logWriter;

    public DadataManagedConnection(String apiUrl, String token, boolean stubMode) {
        this.physicalConnection = new DadataConnectionImpl(apiUrl, token, stubMode);
    }

    @Override
    public Object getConnection(Subject subject, ConnectionRequestInfo cxRequestInfo) throws ResourceException {
        return physicalConnection;
    }

    @Override
    public void destroy() {
        physicalConnection.close();
    }

    @Override
    public void cleanup() {
        physicalConnection.close();
    }

    @Override
    public void associateConnection(Object connection) {
        // outbound-only
    }

    @Override
    public void addConnectionEventListener(ConnectionEventListener listener) {
    }

    @Override
    public void removeConnectionEventListener(ConnectionEventListener listener) {
    }

    @Override
    public XAResource getXAResource() {
        return null;
    }

    @Override
    public LocalTransaction getLocalTransaction() {
        return null;
    }

    @Override
    public ManagedConnectionMetaData getMetaData() {
        return null;
    }

    @Override
    public void setLogWriter(PrintWriter out) {
        this.logWriter = out;
    }

    @Override
    public PrintWriter getLogWriter() {
        return logWriter;
    }
}
