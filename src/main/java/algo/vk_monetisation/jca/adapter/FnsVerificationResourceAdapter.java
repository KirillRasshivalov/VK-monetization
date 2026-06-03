package algo.vk_monetisation.jca.adapter;

import jakarta.resource.ResourceException;
import jakarta.resource.spi.ActivationSpec;
import jakarta.resource.spi.BootstrapContext;
import jakarta.resource.spi.ResourceAdapter;
import jakarta.resource.spi.endpoint.MessageEndpointFactory;
import jakarta.resource.spi.work.Work;
import jakarta.resource.spi.work.WorkManager;
import lombok.extern.slf4j.Slf4j;

import javax.transaction.xa.XAResource;

@Slf4j
public class FnsVerificationResourceAdapter implements ResourceAdapter {

    private BootstrapContext bootstrapContext;
    private WorkManager workManager;

    @Override
    public void start(BootstrapContext bootstrapContext) {
        log.info("FnsVerificationResourceAdapter: starting...");
        this.bootstrapContext = bootstrapContext;
        this.workManager = bootstrapContext.getWorkManager();
        log.info("FnsVerificationResourceAdapter: started successfully");
    }

    @Override
    public void stop() {
        log.info("FnsVerificationResourceAdapter: stopping...");
        bootstrapContext = null;
        workManager = null;
        log.info("FnsVerificationResourceAdapter: stopped");
    }

    @Override
    public void endpointActivation(MessageEndpointFactory endpointFactory, ActivationSpec spec) 
            throws ResourceException {
        log.debug("Endpoint activation requested");
    }

    @Override
    public void endpointDeactivation(MessageEndpointFactory endpointFactory, ActivationSpec spec) {
        log.debug("Endpoint deactivation requested");
    }

    @Override
    public XAResource[] getXAResources(ActivationSpec[] specs) throws ResourceException {
        return new XAResource[0];
    }

    public WorkManager getWorkManager() {
        return workManager;
    }
}

